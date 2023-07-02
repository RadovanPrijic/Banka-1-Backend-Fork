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
    public ResponseEntity<?> getAllAccountsForLoggedInUser() {
        return ResponseEntity.ok(accountService.findAllAccountsForLoggedInUser());
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/user_accounts/{id}")
    public ResponseEntity<?> getAllAccountsForUserById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.findAllAccountsForUserById(id));
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

    @PutMapping("/{accountType}/update_name/{id}/{name}")
    public ResponseEntity<?> updateAccountName(@PathVariable String accountType, @PathVariable Long id, @PathVariable String name) {
        return ResponseEntity.ok(accountService.updateAccountName(accountType, id, name));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping("/{accountType}/update_status/{id}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable String accountType, @PathVariable Long id) {
        return ResponseEntity.ok(accountService.updateAccountStatus(accountType, id));
    }

}
