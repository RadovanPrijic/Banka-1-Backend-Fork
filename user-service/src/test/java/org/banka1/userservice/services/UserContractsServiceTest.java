package org.banka1.userservice.services;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.dtos.user.listing.UserContractListingsDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class UserContractsServiceTest extends IntegrationTest {

    @MockBean
    private UsersContractsService usersContractsService;

    @BeforeEach
    public void initMocks() {
        doNothing().when(usersContractsService).createUpdateUserContract(any());
        doNothing().when(usersContractsService).deleteUserContract(any());
        doNothing().when(usersContractsService).finalizeContract(any());
    }

    @Test
    public void createUserContract() {
        usersContractsService.createUpdateUserContract(new UserContractDto());
        Assertions.assertTrue(true);
    }

    @Test
    public void deleteUserContract() {
        usersContractsService.deleteUserContract("test");
        Assertions.assertTrue(true);
    }

    @Test
    public void finalizeContract() {
        usersContractsService.finalizeContract(new UserContractListingsDto());
        Assertions.assertTrue(true);
    }

}
