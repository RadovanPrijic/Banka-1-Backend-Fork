package org.banka1.userservice.services.services;

import org.banka1.userservice.domains.dtos.user.PasswordDto;
import org.banka1.userservice.domains.dtos.user.UserCreateDto;
import org.banka1.userservice.domains.dtos.user.UserFilterRequest;
import org.banka1.userservice.domains.dtos.user.UserUpdateDto;
import org.banka1.userservice.domains.dtos.user.UserUpdateMyProfileDto;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.exceptions.BadRequestException;
import org.banka1.userservice.domains.exceptions.NotFoundExceptions;
import org.banka1.userservice.domains.exceptions.ValidationException;
import org.banka1.userservice.repositories.UserRepository;
import org.banka1.userservice.services.EmailService;
import org.banka1.userservice.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private UserRepository userRepository;
    private EmailService emailService;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private Page<User> userPage;

    @BeforeEach
    void setUp() {
        this.userRepository = mock(UserRepository.class);
        this.emailService = mock(EmailService.class);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userService = new UserService(userRepository, emailService, passwordEncoder);
    }


    @Test
    void findAllUsersSuccessfully() {
        //given
        var filterChain = new UserFilterRequest();
        when(userRepository.findAll(filterChain.getPredicate(), PageRequest.of(0, 10))).thenReturn(getUsers());

        //when
        var result = userService.getUsers(filterChain, 0, 10);

        //then
        assertEquals(2, result.getContent().size());
    }

    @Test
    void findUserByUserIdSuccessfully() {
        //given
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
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
    void findUserByUserIdThrowsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> userService.findUserById(1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByEmailSuccessfully() {
        //given
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user1));

        //when
        var result = userService.findUserByEmail("email@gmail.com");

        //then
        assertEquals(1, result.getId());
        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("Test", result.getLastName());

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void findUserByEmailThrowsNotFoundException() {
        //given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        //then
        assertThrows(NotFoundExceptions.class, () -> userService.findUserByEmail("email@gmail.com"));

        verify(userRepository, times(1)).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserSuccessfully() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var userCreateDto = new UserCreateDto("Test", "Test", "email@gmail.com",
                "2606999751027", Position.ADMINISTRATOR, "12345", List.of("ROLE_ADMIN"));
        when(userRepository.save(any())).thenReturn(user1);

        var result = userService.createUser(userCreateDto);

        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("Test", result.getFirstName());
        assertEquals("Test", result.getLastName());
        assertTrue(result.isActive());

        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void createUserThrowsValidationExceptionInvalidEmail() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var userCreateDto = new UserCreateDto("Test", "Test", "emailgmail.com",
                "2606999751027", Position.ADMINISTRATOR, "12345", List.of("ROLE_ADMIN"));
        when(userRepository.save(any())).thenReturn(user1);

        assertThrows(ValidationException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void createUserThrowsValidationExceptionInvalidJMBG() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var userCreateDto = new UserCreateDto("Test", "Test", "email@gmail.com",
                "2027", Position.ADMINISTRATOR, "12345", List.of("ROLE_ADMIN"));

        when(userRepository.save(any())).thenReturn(user1);

        assertThrows(ValidationException.class, () -> userService.createUser(userCreateDto));
    }

    @Test
    void updateUserSuccessfully() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var userUpdateDto = new UserUpdateDto("TestUpdate", "TestUpdate", "email@gmail.com", Position.ADMINISTRATOR,
                "123", "Sifra123!", true, List.of("ROLE_ADMIN"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user1);

        var result = userService.updateUser(userUpdateDto, 1L);

        assertEquals("email@gmail.com", result.getEmail());
        assertEquals("TestUpdate", result.getFirstName());
        assertEquals("TestUpdate", result.getLastName());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserThrowsNotFoundException() {
        var userUpdateDto = new UserUpdateDto("TestUpdate", "TestUpdate", "email@gmail.com", Position.ADMINISTRATOR,
                "123", "Sifra123!", true, List.of("ROLE_ADMIN"));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> userService.updateUser(userUpdateDto,1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserThrowsValidationExceptionInvalidPassword() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var userUpdateDto = new UserUpdateDto("TestUpdate", "TestUpdate", "email@gmail.com", Position.ADMINISTRATOR,
                "123", "Sifr!", true, List.of("ROLE_ADMIN"));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(ValidationException.class, () -> userService.updateUser(userUpdateDto,1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserProfileSuccessfully() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var updateMyProfile = new UserUpdateMyProfileDto("TestUpdate", "TestUpdate", "123");
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user1));
        when(userRepository.save(any())).thenReturn(user1);

        var result = userService.updateUserProfile(updateMyProfile);

        assertEquals("TestUpdate", result.getFirstName());
        assertEquals("TestUpdate", result.getLastName());
        assertEquals("123", result.getPhoneNumber());
        assertEquals("email@gmail.com", result.getEmail());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void updateUserProfileThrowsNotFoundException() {
        var updateMyProfile = new UserUpdateMyProfileDto("TestUpdate", "TestUpdate", "123");
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> userService.updateUserProfile(updateMyProfile));

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void resetUserPasswordSuccessfully() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        user1.setSecretKey("secretkey");
        var passwordDto = new PasswordDto("Sifra12345!", "secretkey");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        userService.resetUserPassword(passwordDto, 1L);

        assertNotNull(user1.getPassword());

        verify(userRepository, times(1)).findById(anyLong());
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void resetUserPasswordThrowsNotFoundException() {
        var passwordDto = new PasswordDto("Sifra12345!", "secretkey");
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> userService.resetUserPassword(passwordDto, 1L));

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
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        user1.setSecretKey("secretkey");
        var passwordDto = new PasswordDto("Sifra12345!", "");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThrows(BadRequestException.class, () -> userService.resetUserPassword(passwordDto, 1L));

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void forgotPasswordSuccessfully() {
        var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user1));
        when(userRepository.save(any(User.class))).thenReturn(user1);

        userService.forgotPassword("email@gmail.com");

        assertNotNull(user1.getSecretKey());

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verify(userRepository, times(1)).save(any(User.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void forgotPasswordThrowsNotFoundException() {
        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> userService.forgotPassword("email@gmail.com"));

        verify(userRepository, times(1)).findByEmail(any(String.class));
        verifyNoMoreInteractions(userRepository);
    }

    private Page<User> getUsers() {
        if (userPage == null) {
            var user1 = User.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
            var user2 = User.builder().id(2L).email("email2@gmail.com").firstName("Test").lastName("Test").build();
            userPage = new PageImpl<>(List.of(user1, user2), PageRequest.of(0, 10), 2);
        }
        return userPage;
    }
}