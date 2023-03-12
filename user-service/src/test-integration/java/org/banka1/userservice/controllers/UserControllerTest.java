package org.banka1.userservice.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.UserCreateDto;
import org.banka1.userservice.domains.dtos.user.UserDto;
import org.banka1.userservice.domains.dtos.user.UserFilterRequest;
import org.banka1.userservice.domains.entities.Position;
import org.banka1.userservice.domains.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserControllerTest extends IntegrationTest {

    @Test
    public void getUsersTest() throws Exception {
        UserFilterRequest filterRequest = new UserFilterRequest();
        filterRequest.setPosition(Position.EMPLOYEE);

        mockMvc.perform(post("/api/users?page=0&size=10")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getUserByIdSuccessfully() throws Exception {
        User user = User.builder()
                .firstName("User1")
                .lastName("User1")
                .email("myuser1@mail.com")
                .jmbg("1111112222225")
                .position(Position.EMPLOYEE)
                .phoneNumber("063*******")
                .roles(List.of(User.USER_MODERATOR))
                .build();

        user = userRepository.save(user);
        userRepository.flush();

        MvcResult mvcResult = mockMvc.perform(get("/api/users/" + user.getId())
                        .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        UserDto userDto = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), UserDto.class);

        assertEquals("User1", userDto.getFirstName());
        assertEquals("User1", userDto.getLastName());
        assertEquals("myuser1@mail.com", userDto.getEmail());
        assertEquals(Position.EMPLOYEE, userDto.getPosition());
        assertEquals("1111112222225", userDto.getJmbg());
        assertEquals("063*******", userDto.getPhoneNumber());
        assertTrue(userDto.getRoles().contains(User.USER_MODERATOR));
    }

    @Test
    public void createUserSuccessfully() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("Test user");
        userCreateDto.setLastName("Test user");
        userCreateDto.setEmail("testuser@mail.com");
        userCreateDto.setJmbg("1112223334445");
        userCreateDto.setPosition(Position.EMPLOYEE);
        userCreateDto.setPhoneNumber("063*******");
        userCreateDto.setRoles(List.of(User.USER_MODERATOR));

        MvcResult mvcResult = mockMvc.perform(post("/api/users/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        UserDto userDto = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), UserDto.class);

        assertEquals("Test user", userDto.getFirstName());
        assertEquals("Test user", userDto.getLastName());
        assertEquals("testuser@mail.com", userDto.getEmail());
        assertEquals(Position.EMPLOYEE, userDto.getPosition());
        assertEquals("1112223334445", userDto.getJmbg());
        assertEquals("063*******", userDto.getPhoneNumber());
        assertTrue(userDto.getRoles().contains(User.USER_MODERATOR));
    }

    @Test
    public void createUserInvalidEmail() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("Test user");
        userCreateDto.setLastName("Test user");
        userCreateDto.setEmail("testuser");
        userCreateDto.setJmbg("1112223334447");
        userCreateDto.setPosition(Position.ADMINISTRATOR);
        userCreateDto.setPhoneNumber("063*******");
        userCreateDto.setRoles(List.of(User.USER_MODERATOR));

        MvcResult mvcResult = mockMvc.perform(post("/api/users/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Map<String, String> response = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<Map<String, String>>() {});

        assertEquals("invalid email", response.get("message"));
    }

    @Test
    public void createUserInvalidJmbg() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setFirstName("Test user");
        userCreateDto.setLastName("Test user");
        userCreateDto.setEmail("testu1@mail.com");
        userCreateDto.setJmbg("12345");
        userCreateDto.setPosition(Position.EMPLOYEE);
        userCreateDto.setPhoneNumber("063*******");
        userCreateDto.setRoles(List.of(User.USER_MODERATOR));

        MvcResult mvcResult = mockMvc.perform(post("/api/users/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Map<String, String> response = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<>() {});

        assertEquals("invalid jmbg", response.get("message"));
    }

    @Test
    public void createUserMethodArgumentNotValidException() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();

        MvcResult mvcResult = mockMvc.perform(post("/api/users/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andReturn();

        Map<String, String> response = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<>() {});

        assertEquals("must not be blank", response.get("firstName"));
        assertEquals("must not be blank", response.get("lastName"));
        assertEquals("must not be blank", response.get("email"));
        assertEquals("must not be blank", response.get("jmbg"));
        assertEquals("must not be null", response.get("position"));
        assertEquals("must not be blank", response.get("phoneNumber"));
        assertEquals("must not be null", response.get("roles"));

    }

    @Test
    public void myProfileTest() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/users/my-profile")
                        .header("Authorization", "Bearer " + token))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        UserDto userDto = objectMapper.readValue(mvcResult.getResponse().getContentAsByteArray(), UserDto.class);

        assertEquals("Admin", userDto.getFirstName());
        assertEquals("Admin", userDto.getLastName());
        assertEquals("test@test.com", userDto.getEmail());
        assertEquals(Position.ADMINISTRATOR, userDto.getPosition());
        assertEquals("111222333", userDto.getPhoneNumber());
        assertTrue(userDto.getRoles().contains(User.USER_ADMIN));
    }

}
