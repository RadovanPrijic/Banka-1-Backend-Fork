package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.account.BusinessAccountCreateDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountCreateDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserCreateDto;
import org.banka1.bankservice.domains.dtos.user.UserUpdateDto;
import org.banka1.bankservice.services.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/current_acc/{id}")
    public ResponseEntity<?> getCurrentAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findCurrentAccountById(id));
    }

    @GetMapping("/foreign_currency_acc/{id}")
    public ResponseEntity<?> getForeignCurrencyAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findForeignCurrencyAccountById(id));
    }

    @GetMapping("/business_acc/{id}")
    public ResponseEntity<?> getBusinessAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findBusinessAccountById(id));
    }

    @GetMapping("/user_accounts")
    public ResponseEntity<?> getAllAccountsForUser() {
        return ResponseEntity.ok(accountService.findAllAccountsForUser());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/current_acc/open")
    public ResponseEntity<?> openCurrentAccount(@Valid @RequestBody CurrentAccountCreateDto currentAccountCreateDto) {
        return ResponseEntity.ok(accountService.openCurrentAccount(currentAccountCreateDto));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/foreign_currency_acc/open")
    public ResponseEntity<?> openForeignCurrencyAccount(@Valid @RequestBody ForeignCurrencyAccountCreateDto foreignCurrencyAccountCreateDto) {
        return ResponseEntity.ok(accountService.openForeignCurrencyAccount(foreignCurrencyAccountCreateDto));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/business_acc/open")
    public ResponseEntity<?> openBusinessAccount(@Valid @RequestBody BusinessAccountCreateDto businessAccountCreateDto) {
        return ResponseEntity.ok(accountService.openBusinessAccount(businessAccountCreateDto));
    }

    @PutMapping("/current_acc/update_name/{id}/{name}")
    public ResponseEntity<?> updateCurrentAccountName(@PathVariable Long id, @PathVariable String name) {
        return ResponseEntity.ok(accountService.updateCurrentAccountName(id, name));
    }

    @PutMapping("/current_acc/update_status/{id}")
    public ResponseEntity<?> updateCurrentAccountStatus(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.updateCurrentAccountStatus(id));
    }

    @PutMapping("/foreign_currency_acc/update_name/{id}/{name}")
    public ResponseEntity<?> updateForeignCurrencyAccountName(@PathVariable Long id, @PathVariable String name) {
        return ResponseEntity.ok(accountService.updateForeignCurrencyAccountName(id, name));
    }

    @PutMapping("/foreign_currency_acc/update_status/{id}")
    public ResponseEntity<?> updateForeignCurrencyAccountStatus(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.updateForeignCurrencyAccountStatus(id));
    }

    @PutMapping("/business_acc/update_name/{id}/{name}")
    public ResponseEntity<?> updateBusinessAccountName(@PathVariable Long id, @PathVariable String name) {
        return ResponseEntity.ok(accountService.updateBusinessAccountName(id, name));
    }

    @PutMapping("/business_acc/update_status/{id}")
    public ResponseEntity<?> updateBusinessAccountStatus(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.updateBusinessAccountStatus(id));
    }

}
