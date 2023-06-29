package org.banka1.bankservice.controllers;

import lombok.AllArgsConstructor;
import org.banka1.bankservice.domains.dtos.payment.MoneyTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentCreateDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentReceiverCreateDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentReceiverUpdateDto;
import org.banka1.bankservice.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/bank")
@AllArgsConstructor
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findPaymentById(id));
    }

    @GetMapping("/user_payments")
    public ResponseEntity<?> getAllPaymentsForLoggedInUser() {
        return ResponseEntity.ok(paymentService.findAllPaymentsForLoggedInUser());
    }

    @GetMapping("/account_payments")
    public ResponseEntity<?> getAllPaymentsForAccount(@RequestParam String accountNumber) {
        return ResponseEntity.ok(paymentService.findAllPaymentsForAccount(accountNumber));
    }

    @PostMapping("/make_payment")
    public ResponseEntity<?> makePayment(@Valid @RequestBody PaymentCreateDto paymentCreateDto) {
        return ResponseEntity.ok(paymentService.makePayment(paymentCreateDto));
    }

    @PostMapping("/transfer_money")
    public ResponseEntity<?> transferMoney(@Valid @RequestBody MoneyTransferDto moneyTransferDto) {
        return ResponseEntity.ok(paymentService.transferMoney(moneyTransferDto));
    }

    @GetMapping("/payment_receiver/{id}")
    public ResponseEntity<?> getPaymentReceiverById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findPaymentReceiverById(id));
    }

    @GetMapping("/user_receivers")
    public ResponseEntity<?> getAllPaymentReceiversForLoggedInUser() {
        return ResponseEntity.ok(paymentService.findAllPaymentReceiversForLoggedInUser());
    }

    @PostMapping("/create_receiver")
    public ResponseEntity<?> createPaymentReceiver(@Valid @RequestBody PaymentReceiverCreateDto paymentReceiverCreateDto) {
        return ResponseEntity.ok(paymentService.createPaymentReceiver(paymentReceiverCreateDto));
    }

    @PutMapping("/update_receiver/{id}")
    public ResponseEntity<?> updatePaymentReceiver(@RequestBody PaymentReceiverUpdateDto paymentReceiverUpdateDto, @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.updatePaymentReceiver(paymentReceiverUpdateDto, id));
    }

    @DeleteMapping("/delete_receiver/{id}")
    public ResponseEntity<?> deletePaymentReceiver(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.deletePaymentReceiver(id));
    }

}
