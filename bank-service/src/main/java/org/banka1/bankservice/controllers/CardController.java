package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.card.CardCreateDto;
import org.banka1.bankservice.domains.dtos.card.CardPaymentDto;
import org.banka1.bankservice.services.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
public class CardController {

    private final CardService cardService;

    @GetMapping("/card/{id}")
    public ResponseEntity<?> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.findCardById(id));
    }

    @GetMapping("/user_cards")
    public ResponseEntity<?> getAllCardsForLoggedInUser() {
        return ResponseEntity.ok(cardService.findAllCardsForLoggedInUser());
    }

    @GetMapping("/account_cards")
    public ResponseEntity<?> getAllCardsForAccount(@RequestParam String accountNumber) {
        return ResponseEntity.ok(cardService.findAllCardsForAccount(accountNumber));
    }

    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PostMapping("/create_card")
    public ResponseEntity<?> createCard(@Valid @RequestBody CardCreateDto cardCreateDto) {
        return ResponseEntity.ok(cardService.createCard(cardCreateDto));
    }

    @PostMapping("/make_card_payment")
    public ResponseEntity<?> payWithCard(@Valid @RequestBody CardPaymentDto cardPaymentDto) {
        return ResponseEntity.ok(cardService.payWithCard(cardPaymentDto));
    }

    @PutMapping("/card/update_limit/{id}")
    public ResponseEntity<?> updateAccountLimit(@PathVariable Long id, @RequestParam Double newLimit) {
        return ResponseEntity.ok(cardService.updateCardLimit(id, newLimit));
    }

    @PutMapping("/card/update_status/{id}")
    public ResponseEntity<?> updateAccountStatus(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.updateCardStatus(id));
    }

}
