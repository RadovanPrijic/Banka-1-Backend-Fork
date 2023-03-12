package org.banka1.userservice.controllers;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.UserFilterRequest;
import org.banka1.userservice.domains.entities.Position;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

}
