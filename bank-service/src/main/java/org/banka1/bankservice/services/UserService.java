package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.banka1.bankservice.domains.dtos.user.*;
import org.banka1.bankservice.domains.entities.Position;
import org.banka1.bankservice.domains.entities.User;
import org.banka1.bankservice.domains.exceptions.BadRequestException;
import org.banka1.bankservice.domains.exceptions.ForbiddenException;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.UserMapper;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${password.reset.endpoint}")
    private String passwordResetEndpoint;

    @Value("${password.activate.endpoint}")
    private String passwordActivateEndpoint;

    private final Pattern emailPattern = Pattern.compile("^[a-z0-9_.-]+@(.+)$");
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,32}$");
    // Lozinka mora sadrzati barem po jedno malo slovo, veliko slovo, broj, specijalni karakter
    // i mora imati duzinu barem 8 karaktera

    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        if(!emailPattern.matcher(userCreateDto.getEmail()).matches()) {
            throw new ValidationException("Invalid email address form.");
        }

        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        String secretKey = RandomStringUtils.randomNumeric(6);
        user.setSecretKey(secretKey);

        userRepository.saveAndFlush(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordActivateEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Activate your bank account", text);

        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User has not been found."));

        if(userUpdateDto.getPassword() != null && !passwordPattern.matcher(userUpdateDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be 8 to 32 characters long.");
        }

        UserMapper.INSTANCE.updateUserFromUserUpdateDto(user, userUpdateDto);

        if(userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public void resetUserPassword(PasswordDto passwordDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User has not been found."));

        if(!passwordPattern.matcher(passwordDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be at least 8 characters long.");
        }

        if(user.getSecretKey() == null || !user.getSecretKey().equals(passwordDto.getSecretKey())) {
            throw new BadRequestException("Invalid secret key.");
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        user.setSecretKey(null);

        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        String secretKey = RandomStringUtils.randomAlphabetic(6);
        user.setSecretKey(secretKey);
        userRepository.save(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordResetEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Reset your bank account password", text);
    }

    public UserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    public UserDto returnUserProfile(){
        Optional<User> user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

}
