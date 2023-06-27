package org.banka1.userservice.controllers;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.listing.UserListingCreateDto;
import org.banka1.userservice.domains.dtos.user.listing.UserListingDto;
import org.banka1.userservice.services.UserListingService;
import org.banka1.userservice.services.UserListingServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class UserListingControllerTest extends IntegrationTest {

    @MockBean
    private UserListingService userListingService;

    @BeforeEach
    public void setUpMocks() {
        when(userListingService.getListingsByUser(anyLong())).thenReturn(Collections.emptyList());
        when(userListingService.createUserListing(anyLong(), any())).thenReturn(new UserListingDto());
        when(userListingService.updateUserListing(anyLong(), any())).thenReturn(new UserListingDto());
    }

    @Test
    public void getUserListingByUserId() throws Exception {
        Long id = userRepository.findByEmail("test@test.com").orElse(null).getId();

        mockMvc.perform(get("/api/user-listings?userId=" + id)
                .header("Authorization", "Bearer " + adminToken)
                .contentType("application/json"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn();
    }

    @Test
    public void createUserListing() throws Exception {
        UserListingCreateDto createRequest = new UserListingCreateDto();
        Long id = userRepository.findByEmail("test@test.com").orElse(null).getId();

        mockMvc.perform(post("/api/user-listings/create?userId=" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateUserListing() throws Exception {
        mockMvc.perform(put("/api/user-listings/update/1?newQuantity=10")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

}
