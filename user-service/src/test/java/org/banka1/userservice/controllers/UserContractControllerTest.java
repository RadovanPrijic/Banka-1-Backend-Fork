package org.banka1.userservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.dtos.user.listing.UserContractListingsDto;
import org.banka1.userservice.domains.entities.UserContract;
import org.banka1.userservice.services.UsersContractsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserContractControllerTest extends IntegrationTest {

    @MockBean
    private UsersContractsService usersContractsService;

    @BeforeEach
    public void initMocks() {
        doNothing().when(usersContractsService).createUpdateUserContract(any());
        doNothing().when(usersContractsService).deleteUserContract(any());
        doNothing().when(usersContractsService).finalizeContract(any());
    }

    @Test
    public void createUserContract() throws Exception {
        UserContractDto contractDto = new UserContractDto();

        mockMvc.perform(post("/api/users-contracts/reserve-assets")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contractDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void deleteUserContract() throws Exception {
        mockMvc.perform(delete("/api/users-contracts/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void finalizeContract() throws Exception {
        UserContractListingsDto contractListingsDto = new UserContractListingsDto();

        mockMvc.perform(post("/api/users-contracts/finalize-contract")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(contractListingsDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

}
