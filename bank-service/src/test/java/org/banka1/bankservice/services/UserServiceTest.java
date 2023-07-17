package org.banka1.bankservice.services;

import org.banka1.bankservice.domains.dtos.user.PasswordDto;
import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserFilterRequest;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.domains.entities.account.Company;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Department;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.entities.user.Position;
import org.banka1.bankservice.domains.exceptions.BadRequestException;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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

    @AfterAll
    public static void clearCache(){
        Mockito.clearAllCaches();
    }
    @Test
    void createUserSuccessfully() {

        var user1 = BankUser.builder()
                .id(1L)
                .firstName("Zoran")
                .lastName("Stosic")
                .birthDate(LocalDate.of(1981, 1, 29))
                .gender(Gender.MALE)
                .email("admin@admin.com")
                .phoneNumber("0622495678")
                .homeAddress("Bulevar Kralja Aleksandra 52")
                .password(passwordEncoder.encode("Admin1234!"))
                .position(Position.SYSTEM_ADMIN)
                .department(Department.IT)
                .roles(List.of("ROLE_EMPLOYEE"))
                .build();

        var userCreateDto = new UserCreateDto("Zoran", "Stosic", LocalDate.of(1981, 1, 29),
                Gender.MALE, "admin@admin.com","0622495689", "Bulevar Kralja Aleksandra 52",  List.of("ROLE_EMPLOYEE"));

        when(userRepository.saveAndFlush(any())).thenReturn(user1);

        var result = userService.createUser(userCreateDto);

        assertEquals("admin@admin.com", result.getEmail());
        assertEquals("Zoran", result.getFirstName());
        assertEquals("Stosic", result.getLastName());
        assertEquals("0622495689", result.getPhoneNumber());
        assertEquals("Bulevar Kralja Aleksandra 52", result.getHomeAddress());


        verify(userRepository, times(1)).saveAndFlush(any(BankUser.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserThrowsValidationExceptionInvalidEmail() {
        var userCreateDto = new UserCreateDto("Zoran", "Stosic", LocalDate.of(1981, 1, 29),
                Gender.MALE, "admindrrrradmin.com","0622495689", "Bulevar Kralja Aleksandra 52",  List.of("ROLE_EMPLOYEE"));
        when(userRepository.saveAndFlush(any())).thenReturn(null);

        assertThrows(ValidationException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void updateUserSuccessfully() {
//        var user1 = BankUser.builder()
//                .id(1L)
//                .firstName("Zoran")
//                .lastName("Stosic")
//                .birthDate(LocalDate.of(1981, 1, 29))
//                .gender(Gender.MALE)
//                .email("admin@admin.com")
//                .phoneNumber("0622495678")
//                .homeAddress("Bulevar Kralja Aleksandra 52")
//                .password(passwordEncoder.encode("Admin1234!"))
//                .position(Position.SYSTEM_ADMIN)
//                .department(Department.IT)
//                .roles(List.of("ROLE_EMPLOYEE"))
//                .build();
//
//        var userUpdateDto = new UserUpdateDto("Stosic", Gender.MALE, "0622495689",
//                "Bulevar Kralja Aleksandra 52", "Admin1234!", List.of("ROLE_EMPLOYEE"));
//
//        //given
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
//        when(userRepository.save(any())).thenReturn(user1);
//
//        //when
//        var result = userService.updateUser(userUpdateDto, 1L);
//
//        assertEquals("Stosic", result.getLastName());
//        assertEquals(Gender.MALE, result.getGender());
//        assertEquals("0622495689", result.getPhoneNumber());
//        assertEquals("Bulevar Kralja Aleksandra 52", result.getHomeAddress());
//        assertEquals("ROLE_EMPLOYEE", result.getRoles().get(0));
//
//        verify(userRepository, times(1)).findById(anyLong());
//        verify(userRepository, times(1)).save(any(BankUser.class));
//        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserThrowsNotFoundException() {
        var userUpdateDto = new UserUpdateDto("Stosic", Gender.MALE, "0622495689",
                "Bulevar Kralja Aleksandra 52", "admin", List.of("ROLE_EMPLOYEE"));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(userUpdateDto,1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserThrowsValidationExceptionInvalidPassword() {
        var user1 = BankUser.builder()
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
        var userUpdateDto = new UserUpdateDto("Stosic", Gender.MALE, "0622495689",
                "Bulevar Kralja Aleksandra 52", "admin", List.of("ROLE_EMPLOYEE"));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> userService.updateUser(userUpdateDto,1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void resetUserPasswordSuccessfully() {
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        user1.setSecretKey("secretkey");
        var passwordDto = new PasswordDto("Sifra12345!", "secretkey");

        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(BankUser.class))).thenReturn(user1);

        //when
        userService.resetUserPassword(passwordDto, 1L);

        //then
        assertNotNull(user1.getPassword());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(BankUser.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void resetUserPasswordThrowsNotFoundException() {
        var passwordDto = new PasswordDto("Sifra12345!", "secretkey");
        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        assertThrows(NotFoundException.class, () -> userService.resetUserPassword(passwordDto, 1L));

        //then
        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void resetUserPasswordThrowsValidationExceptionInvalidPassword() {
        var passwordDto = new PasswordDto("S12345!", "secretkey");

        assertThrows(ValidationException.class, () -> userService.resetUserPassword(passwordDto, 1L));
    }

    @Test
    void resetUserPasswordThrowsBadRequestInvalidSecretKey() {
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        user1.setSecretKey("secretkey");
        var passwordDto = new PasswordDto("Sifra12345!", "");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(BadRequestException.class, () -> userService.resetUserPassword(passwordDto, 1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void forgotPasswordSuccessfully() {
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user1));
        when(userRepository.save(any(BankUser.class))).thenReturn(user1);

        userService.forgotPassword("email@gmail.com");

        assertNotNull(user1.getSecretKey());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(userRepository, times(1)).save(any(BankUser.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void forgotPasswordThrowsNotFoundException() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.forgotPassword("email@gmail.com"));

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByIdSuccessfully() {
        //given
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user1));

        //when
        var result = userService.findUserById(1L);

        //then
        assertEquals(1, result.getId());
        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("Test", result.getLastName());

        verify(userRepository, times(1)).findById((any(Long.class)));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByIdThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void findUserByEmailSuccessfully() {
        //given
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user1));

        //when
        var result = userService.findUserByEmail("email@gmail.com");

        //then
        assertEquals(1, result.getId());
        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("Test", result.getLastName());

        verify(userRepository, times(1)).findByEmail((any(String.class)));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByEmailThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserByEmail(anyString()));

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }
    @Test
    void findAllClientsSuccessfully(){
        //given
        when(userRepository.findAll()).thenReturn(getClients());
        //when
        var result = userService.findAllClients();
        //then
        assertEquals(3,result.size());
    }

    @Test
    void findAllClientsFilteredSuccessfully() {
        //given
        var filterChain = new UserFilterRequest();
        when(userRepository.findAll(filterChain.getPredicate())).thenReturn(getClients());

        //when
        var result = userService.findAllClientsFiltered(filterChain);

        //then
        assertEquals(3, result.size());
    }

    @Test
    void returnUserProfileSuccessfully() {
        //given
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user1));

        //when
        var result = userService.returnUserProfile();

        //then
        assertEquals(1, result.getId());
        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("Test", result.getLastName());

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void returnUserProfileThrowsNotFoundException() {
        //given
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(NotFoundException.class, () -> userService.returnUserProfile());

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void loadUserByUsernameSuccessfully() {
        //given
        String password = passwordEncoder.encode("admin");
        var user1 = BankUser.builder()
                .firstName("Zoran")
                .lastName("Stosic")
                .birthDate(LocalDate.of(1981, 1, 29))
                .gender(Gender.MALE)
                .email("admin@admin.com")
                .phoneNumber("0622495678")
                .homeAddress("Bulevar Kralja Aleksandra 52")
                .password(password)
                .position(Position.SYSTEM_ADMIN)
                .department(Department.IT)
                .roles(List.of("ROLE_EMPLOYEE"))
                .build();

        //when
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user1));
        //when
        var result = userService.loadUserByUsername("admin@admin.com");

        //then
        assertEquals("admin@admin.com", result.getUsername());
        assertEquals(password, result.getPassword());
        assertTrue(result.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals("ROLE_EMPLOYEE")));


        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    void loadUserByUsernameThrowsUsernameNotFoundException() {
        //given
//        String password = passwordEncoder.encode("admin1234");
//        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test")
//                .roles(List.of("ADMINISTRATOR")).password(password).build();

        //when
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> userService.loadUserByUsername("email@gmail.com"));


        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
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
