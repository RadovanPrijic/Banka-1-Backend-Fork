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
        User user = User.builder()
                .firstName("Admin")
                .lastName("Admin")
                .email("admin@admin.com")
                .position(Position.ADMINISTRATOR)
                .jmbg("123456789")
                .phoneNumber("063*********")
                .password(passwordEncoder.encode("admin1234"))
                .roles(List.of("ROLE_ADMIN"))
                .active(true)
                .build();

        userRepository.save(user);
        System.out.println("Data loaded");
    }
}
