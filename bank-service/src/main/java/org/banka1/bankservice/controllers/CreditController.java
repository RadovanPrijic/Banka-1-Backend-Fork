package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.services.CreditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/create_credit_request")
    public ResponseEntity<?> createCreditRequest(@Valid @RequestBody CreditRequestCreateDto creditRequestCreateDto) {
        return ResponseEntity.ok(creditService.createCreditRequest(creditRequestCreateDto));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/approve_credit_request/{requestId}")
    public ResponseEntity<?> approveCreditRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(creditService.approveCreditRequest(requestId));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/deny_credit_request/{requestId}")
    public ResponseEntity<?> denyCreditRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(creditService.denyCreditRequest(requestId));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping("/waiting_credit_requests")
    public ResponseEntity<?> getAllWaitingCreditRequests() {
        return ResponseEntity.ok(creditService.findAllWaitingCreditRequests());
    }

    @GetMapping("/pay_credit_installment/{creditId}")
    public ResponseEntity<?> payCreditInstallment(@PathVariable Long creditId) {
        return ResponseEntity.ok(creditService.payCreditInstallment(creditId));
    }

    @GetMapping("/credit_request/{id}")
    public ResponseEntity<?> getCreditRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(creditService.findCreditRequestById(id));
    }

    @GetMapping("/user_credit_requests")
    public ResponseEntity<?> getAllCreditRequestsForLoggedInUser() {
        return ResponseEntity.ok(creditService.findAllCreditRequestsForLoggedInUser());
    }

    @GetMapping("/account_credit_requests")
    public ResponseEntity<?> getAllCreditRequestsForAccount(@RequestParam String accountNumber) {
        return ResponseEntity.ok(creditService.findAllCreditRequestsForAccount(accountNumber));
    }

    @GetMapping("/credit/{id}")
    public ResponseEntity<?> getCreditById(@PathVariable Long id) {
        return ResponseEntity.ok(creditService.findCreditById(id));
    }

    @GetMapping("/user_credits")
    public ResponseEntity<?> getAllCreditsForLoggedInUser() {
        return ResponseEntity.ok(creditService.findAllCreditsForLoggedInUser());
    }

    @GetMapping("/account_credits")
    public ResponseEntity<?> getAllCreditsForAccount(@RequestParam String accountNumber) {
        return ResponseEntity.ok(creditService.findAllCreditsForAccount(accountNumber));
    }

    @GetMapping("/credit_installments/{creditId}")
    public ResponseEntity<?> getAllCreditInstallmentsForCredit(@PathVariable Long creditId) {
        return ResponseEntity.ok(creditService.findAllCreditInstallmentsForCredit(creditId));
    }

}
