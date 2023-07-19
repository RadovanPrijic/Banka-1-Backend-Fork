package org.banka1.bankservice.bootstrap;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.entities.account.Company;
import org.banka1.bankservice.domains.entities.user.*;
import org.banka1.bankservice.repositories.CompanyRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.CurrencyExchangeService;
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
    private final CompanyRepository companyRepository;
    private final CurrencyExchangeService currencyExchangeService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.findAll().isEmpty())
            return;

        currencyExchangeService.loadForex();
        System.out.println("Exchange pairs loaded");

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

//        BankUser employee2 = BankUser.builder()
//                .firstName("Luka")
//                .lastName("Lukacevic")
//                .birthDate(LocalDate.of(1983, 6, 1))
//                .gender(Gender.MALE)
//                .email("luka.lukacevic@useremail.com")
//                .phoneNumber("0651452580")
//                .homeAddress("Bulevar Despota Stefana 37")
//                .password(passwordEncoder.encode("lukalukacevic"))
//                .position(Position.MANAGER)
//                .department(Department.FINANCE)
//                .roles(List.of("ROLE_EMPLOYEE"))
//                .build();

        BankUser client1 = BankUser.builder()
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@useremail.com")
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
                .email("petar.petrovic@useremail.com")
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
                .email("jovana.jovanovic@useremail.com")
                .phoneNumber("0633456751")
                .homeAddress("Budimska 12")
                .password(passwordEncoder.encode("jovanajovanovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();

        Company company1 = Company.builder()
                .companyName("YourHome Real Estate Agency")
                .phoneNumber("0621586732")
                .faxNumber("0112030402")
                .vatIdNumber(203045644)
                .identificationNumber(12289156)
                .activityCode(6831)
                .registryNumber(173240122)
                .build();

        Company company2 = Company.builder()
                .companyName("Inspirex IT Solutions")
                .phoneNumber("0657235934")
                .faxNumber("0212295621")
                .vatIdNumber(101017533)
                .identificationNumber(17328905)
                .activityCode(6201)
                .registryNumber(330602854)
                .build();

        Company company3 = Company.builder()
                .companyName("Ivanovic & Partners Legal Services")
                .phoneNumber("0636628513")
                .faxNumber("0118904768")
                .vatIdNumber(521037997)
                .identificationNumber(34152290)
                .activityCode(6910)
                .registryNumber(130501701)
                .build();

        userRepository.save(employee1);
//        userRepository.save(employee2);
        userRepository.save(client1);
        userRepository.save(client2);
        userRepository.save(client3);
        companyRepository.save(company1);
        companyRepository.save(company2);
        companyRepository.save(company3);

        System.out.println("Data loaded");
    }

}
