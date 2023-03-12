package org.banka1.userservice.bootstrap;

import lombok.AllArgsConstructor;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User admin = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("admin@admin.com")
                .position(Position.ADMINISTRATOR)
                .jmbg("1111111111")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("admin1234"))
                .roles(List.of("ROLE_ADMIN"))
                .active(true)
                .build();

        User user1 = User.builder()
                .firstName("User1")
                .lastName("User1")
                .email("user1@user1.com")
                .position(Position.EMPLOYEE)
                .jmbg("2222222222")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user1"))
                .roles(List.of("ROLE_MODERATOR"))
                .active(true)
                .build();

        User user2 = User.builder()
                .firstName("User2")
                .lastName("User2")
                .email("user2@user2.com")
                .position(Position.EMPLOYEE)
                .jmbg("3333333333")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user3"))
                .roles(List.of("ROLE_MODERATOR"))
                .active(true)
                .build();

        User user3 = User.builder()
                .firstName("User3")
                .lastName("User3")
                .email("user3@user3.com")
                .position(Position.EMPLOYEE)
                .jmbg("4444444444")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("user3"))
                .roles(List.of("ROLE_MODERATOR"))
                .active(true)
                .build();

        userRepository.save(admin);
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        System.out.println("Data loaded");
    }
}
