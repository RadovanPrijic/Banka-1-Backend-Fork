package org.banka1.bankservice.services;

import org.banka1.bankservice.domains.entities.account.Company;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Department;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserRepository userRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private UserService userService;


    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.emailService = mock(EmailService.class);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = new UserService(userRepository, emailService, passwordEncoder);
    }

    @Test
    void findAllClientsSuccesfully(){
        //given
        when(userRepository.findAll()).thenReturn(getClients());
        //when
        var result = userService.findAllClients();
        //then
        assertEquals(3,result.size());
    }

    List<BankUser> getClients(){
        List<BankUser> list = new ArrayList<>();
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

        list.add(employee1);
        list.add(client1);
        list.add(client2);
        list.add(client3);

        return list;
    }

}
