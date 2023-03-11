package org.banka1.userservice.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.banka1.userservice.domains.dtos.user.*;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.exceptions.BadRequestException;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.domains.exceptions.ValidationException;
import org.banka1.userservice.domains.mappers.UserMapper;
import org.banka1.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final Pattern jmbgPattern = Pattern.compile("[0-9]{13}");
    // Lozinka mora sadrzati barem po jedno malo slovo, veliko slovo, broj, specijalni karakter
    // i mora imati duzinu barem 8 karaktera
    private final Pattern passwordPattern = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,}$");

    public UserService(UserRepository userRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserDto> getUsers(UserFilterRequest filterRequest, Integer page, Integer size) {
        Page<User> users = userRepository.findAll(
                filterRequest.getPredicate(),
                PageRequest.of(page, size)
        );

        return new PageImpl<>(
                users.stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList()),
                PageRequest.of(page, size),
                users.getContent().size()
        );
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        // validate email and jmbg
        if(!emailPattern.matcher(userCreateDto.getEmail()).matches()) {
            throw new ValidationException("invalid email");
        }
        if(!jmbgPattern.matcher(userCreateDto.getJmbg()).matches()) {
            throw new ValidationException("invalid jmbg");
        }

        User user = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        String secretKey = RandomStringUtils.randomNumeric(6);

        user.setActive(true);
        user.setSecretKey(secretKey);

        userRepository.save(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordActivateEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Activate account", text);

        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundExceptions("user not found"));

        if(userUpdateDto.getPassword() != null && !passwordPattern.matcher(userUpdateDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be at least 8 characters long.");
        }

        UserMapper.INSTANCE.updateUserFromUserUpdateDto(user, userUpdateDto);

        if(userUpdateDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public UserDto updateUserProfile(UserUpdateMyProfileDto userUpdateMyProfileDto) {
        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new NotFoundExceptions("user not found"));
        UserMapper.INSTANCE.updateUserFromUserUpdateMyProfileDto(user, userUpdateMyProfileDto);

        userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDto(user);
    }

    public void resetUserPassword(PasswordDto passwordDto, Long id) {
        if(!passwordPattern.matcher(passwordDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be at least 8 characters long.");
        }

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundExceptions("user not found"));

        if(user.getSecretKey() == null || !user.getSecretKey().equals(passwordDto.getSecretKey())) {
            throw new BadRequestException("invalid secret key");
        }

        user.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        user.setSecretKey(null);

        userRepository.save(user);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundExceptions("user not found"));

        String secretKey = RandomStringUtils.randomAlphabetic(6);
        user.setSecretKey(secretKey);
        userRepository.save(user);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordResetEndpoint + "/" + user.getId();
        emailService.sendEmail(user.getEmail(), "Reset password", text);
    }

    public UserDto findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundExceptions("user not found"));
    }

    public UserDto findUserByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundExceptions("user not found"));
    }

    public UserDto returnUserProfile(){
        Optional<User> user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundExceptions("user not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundExceptions("user not found"));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

}
