package org.banka1.userservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.userservice.domains.dtos.user.UserContractDto;
import org.banka1.userservice.domains.dtos.user.listing.UserContractListingsDto;
import org.banka1.userservice.services.UsersContractsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users-contracts")
@AllArgsConstructor
@CrossOrigin
public class UsersContractsController {

    private final UsersContractsService usersContractsService;

    @PostMapping("/reserve-assets")
    public ResponseEntity<?> createUpdateUserContract(@Valid @RequestBody UserContractDto userContractDto) {
        usersContractsService.createUpdateUserContract(userContractDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{contractId}")
    public ResponseEntity<?> deleteContract(@PathVariable String contractId) {
        usersContractsService.deleteUserContract(contractId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/finalize-contract")
    public ResponseEntity<?> finalizeTheContract(@Valid @RequestBody UserContractListingsDto userContractListingsDto) {
        usersContractsService.finalizeContract(userContractListingsDto);
        return ResponseEntity.ok().build();
    }
}
