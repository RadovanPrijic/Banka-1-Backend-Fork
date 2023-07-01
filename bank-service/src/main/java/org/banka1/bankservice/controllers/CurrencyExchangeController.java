package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferConfirmDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferCreateDto;
import org.banka1.bankservice.services.CurrencyExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
public class CurrencyExchangeController {

    private final CurrencyExchangeService currencyExchangeService;

    @GetMapping("/conversion/{id}")
    public ResponseEntity<?> getConversionTransferById(@PathVariable Long id) {
        return ResponseEntity.ok(currencyExchangeService.findConversionTransferById(id));
    }

    @GetMapping("/user_conversions")
    public ResponseEntity<?> getAllConversionTransfersForLoggedInUser() {
        return ResponseEntity.ok(currencyExchangeService.findAllConversionTransfersForLoggedInUser());
    }

    @GetMapping("/account_conversions")
    public ResponseEntity<?> getAllConversionTransfersForAccount(@RequestParam String accountNumber) {
        return ResponseEntity.ok(currencyExchangeService.findAllConversionTransfersForAccount(accountNumber));
    }

    @GetMapping("/exchange_pairs")
    public ResponseEntity<?> getAllExchangePairs() {
        return ResponseEntity.ok(currencyExchangeService.findAllExchangePairs());
    }

    @PostMapping("/convert_money")
    public ResponseEntity<?> convertMoney(@Valid @RequestBody ConversionTransferCreateDto conversionTransferCreateDto) {
        return ResponseEntity.ok(currencyExchangeService.convertMoney(conversionTransferCreateDto));
    }

    @PostMapping("/confirm_conversion")
    public ResponseEntity<?> confirmConversionTransfer(@Valid @RequestBody ConversionTransferConfirmDto conversionTransferConfirmDto) {
        return ResponseEntity.ok(currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto));
    }

}
