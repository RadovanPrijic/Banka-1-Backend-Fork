package org.banka1.userservice.services;

import org.banka1.userservice.IntegrationTest;
import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.dtos.user.listing.UserContractListingsDto;
import org.banka1.userservice.domains.entities.User;
import org.banka1.userservice.domains.entities.UserContract;
import org.banka1.userservice.repositories.UserContractRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

public class UserContractsServiceTest extends IntegrationTest {

    @Autowired
    private UsersContractsService usersContractsService;
    @Autowired
    private UserContractRepository userContractRepository;

    @Test
    public void createUserContract() {
        UserContractDto userContractDto = new UserContractDto();
        userContractDto.setContractId("contract-id-created");
        userContractDto.setPrice(200.0);

        usersContractsService.createUpdateUserContract(userContractDto);
        UserContract createdContract = userContractRepository.findByContractId("contract-id-created");

        Assertions.assertEquals(200.0, createdContract.getPrice());
    }

    @Test
    public void updateUserContract() {
        UserContract userContract = UserContract.builder()
                .contractId("contract-id")
                .price(150.0)
                .build();
        userContractRepository.save(userContract);

        UserContractDto userContractDto = new UserContractDto();
        userContractDto.setContractId("contract-id");
        userContractDto.setPrice(200.0);

        usersContractsService.createUpdateUserContract(userContractDto);
        UserContract updatedContract = userContractRepository.findByContractId("contract-id");

        Assertions.assertEquals(200.0, updatedContract.getPrice());
    }

    @Test
    public void deleteUserContract() {
        UserContract userContract = UserContract.builder()
                .contractId("contract-id-LAKI")
                .price(150.0)
                .build();
        userContractRepository.save(userContract);

        usersContractsService.deleteUserContract("contract-id-LAKI");

        UserContract contractNotFound = userContractRepository.findByContractId("contract-id-LAKI");
        Assertions.assertNull(contractNotFound);
    }

    @Test
    public void finalizeContract() {
        UserContract userContract = UserContract.builder()
                .contractId("contract-id-VUK")
                .price(150.0)
                .build();
        userContractRepository.save(userContract);

        User user = userRepository.findByEmail("supervisor@supervisor.com").get();

        UserContractListingsDto request = new UserContractListingsDto();
        request.setContractId("contract-id-VUK");
        request.setUserId(user.getId());
        request.setSellPrice(120.0);
        request.setStocks(new ArrayList<>());

        usersContractsService.finalizeContract(request);
    }

}
