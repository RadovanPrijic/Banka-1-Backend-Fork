package org.banka1.bankservice.bootstrap;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.entities.BankUser;
import org.banka1.bankservice.domains.entities.Department;
import org.banka1.bankservice.domains.entities.Gender;
import org.banka1.bankservice.domains.entities.Position;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@AllArgsConstructor
//@Profile("local")
@Profile("!test_it")
public class BootstrapData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        BankUser employee1 = BankUser.builder()
                .firstName("Zoran")
                .lastName("Stosic")
                .birthDate(LocalDate.of(1981, 1, 29))
                .gender(Gender.MALE)
                .email("admin@admin.com")
                .phoneNumber("0622495678")
                .homeAddress("Bulevar Kralja Aleksandra 52")
                .password(passwordEncoder.encode("admin"))
                .position(Position.SYSTEM_ADMIN)
                .department(Department.IT)
                .roles(List.of("ROLE_EMPLOYEE"))
                .build();

        BankUser employee2 = BankUser.builder()
                .firstName("Luka")
                .lastName("Lukacevic")
                .birthDate(LocalDate.of(1983, 6, 1))
                .gender(Gender.MALE)
                .email("luka.lukacevic@gmail.com")
                .phoneNumber("0651452580")
                .homeAddress("Bulevar Despota Stefana 37")
                .password(passwordEncoder.encode("lukalukacevic"))
                .position(Position.MANAGER)
                .department(Department.FINANCE)
                .roles(List.of("ROLE_EMPLOYEE"))
                .build();

        BankUser client1 = BankUser.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@gmail.com")
                .phoneNumber("0651678989")
                .homeAddress("Njegoseva 25")
                .password(passwordEncoder.encode("markomarkovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();

        BankUser client2 = BankUser.builder()
                .firstName("Petar")
                .lastName("Petrovic")
                .birthDate(LocalDate.of(1986, 3, 17))
                .gender(Gender.MALE)
                .email("petar.petrovic@gmail.com")
                .phoneNumber("0651224390")
                .homeAddress("Kralja Milana 34")
                .password(passwordEncoder.encode("petarpetrovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();

        BankUser client3 = BankUser.builder()
                .firstName("Jovana")
                .lastName("Jovanovic")
                .birthDate(LocalDate.of(1988, 9, 11))
                .gender(Gender.FEMALE)
                .email("jovana.jovanovic@gmail.com")
                .phoneNumber("0633456751")
                .homeAddress("Budimska 12")
                .password(passwordEncoder.encode("jovanajovanovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();

        userRepository.save(employee1);
        userRepository.save(employee2);
        userRepository.save(client1);
        userRepository.save(client2);
        userRepository.save(client3);

        System.out.println("Data loaded");
    }
}
