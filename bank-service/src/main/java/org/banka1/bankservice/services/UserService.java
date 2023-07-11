package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.banka1.bankservice.domains.dtos.user.*;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.UserRole;
import org.banka1.bankservice.domains.exceptions.BadRequestException;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.UserMapper;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

        BankUser bankUser = UserMapper.INSTANCE.userCreateDtoToUser(userCreateDto);
        String secretKey = RandomStringUtils.randomNumeric(6);
        bankUser.setSecretKey(secretKey);

        userRepository.saveAndFlush(bankUser);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordActivateEndpoint + "/" + bankUser.getId();
        emailService.sendEmail(bankUser.getEmail(), "Activate your bank account", text);

        return UserMapper.INSTANCE.userToUserDto(bankUser);
    }

    public UserDto updateUser(UserUpdateDto userUpdateDto, Long id) {
        BankUser bankUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User has not been found."));

        if(userUpdateDto.getPassword() != null && !passwordPattern.matcher(userUpdateDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be 8 to 32 characters long.");
        }

        UserMapper.INSTANCE.updateUserFromUserUpdateDto(bankUser, userUpdateDto);

        if(userUpdateDto.getPassword() != null) {
            bankUser.setPassword(passwordEncoder.encode(bankUser.getPassword()));
        }

        userRepository.save(bankUser);
        return UserMapper.INSTANCE.userToUserDto(bankUser);
    }

    public void resetUserPassword(PasswordDto passwordDto, Long id) {
        BankUser bankUser = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User has not been found."));

        if(!passwordPattern.matcher(passwordDto.getPassword()).matches()) {
            throw new ValidationException("Invalid password format. Password has to contain" +
                    " at least one of each: uppercase letter, lowercase letter, number, and special character. " +
                    "It also has to be at least 8 characters long.");
        }

        if(bankUser.getSecretKey() == null || !bankUser.getSecretKey().equals(passwordDto.getSecretKey())) {
            throw new BadRequestException("Invalid secret key.");
        }

        bankUser.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
        bankUser.setSecretKey(null);

        userRepository.save(bankUser);
    }

    public void forgotPassword(String email) {
        BankUser bankUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        String secretKey = RandomStringUtils.randomAlphabetic(6);
        bankUser.setSecretKey(secretKey);
        userRepository.save(bankUser);

        String text = "Secret key: " + secretKey + "\n" + "Link: " + passwordResetEndpoint + "/" + bankUser.getId();
        emailService.sendEmail(bankUser.getEmail(), "Reset your bank account password", text);
    }

    public UserDto findUserById(Long id) {
        Optional<BankUser> user = userRepository.findById(id);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    public UserDto findUserByEmail(String email) {
        Optional<BankUser> user = userRepository.findByEmail(email);
        return user.map(UserMapper.INSTANCE::userToUserDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    public List<UserDto> findAllClients() {
        List<UserDto> userDtos = new ArrayList<>();
        List<BankUser> users = userRepository.findAll();

        users.forEach(user -> {
            if(user.getRoles().contains("ROLE_CLIENT"))
                userDtos.add(UserMapper.INSTANCE.userToUserDto(user));
        });

        return userDtos;
    }

    public List<UserDto> findAllClientsFiltered(UserFilterRequest userFilterRequest) {
        Iterable<BankUser> result = userRepository.findAll(userFilterRequest.getPredicate());

        List<BankUser> filteredUsersList = new ArrayList<>();
        result.forEach(user -> {
            if(user.getRoles().contains("ROLE_CLIENT"))
                filteredUsersList.add(user);
        });

        return filteredUsersList.stream().map(UserMapper.INSTANCE::userToUserDto).collect(Collectors.toList());
    }

    public UserMyProfileDto returnUserProfile(){
        Optional<BankUser> user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return user.map(UserMapper.INSTANCE::userToUserMyProfileDto).orElseThrow(() -> new NotFoundException("User has not been found."));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        BankUser bankUser = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        return new org.springframework.security.core.userdetails.User(bankUser.getEmail(), bankUser.getPassword(), bankUser.getAuthorities());
    }

}
