package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.mappers.PaymentMapper;
import org.banka1.bankservice.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentReceiverRepository paymentReceiverRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final BusinessAccountRepository businessAccountRepository;
    private final ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentReceiverRepository paymentReceiverRepository,
                          CurrentAccountRepository currentAccountRepository, ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                          BusinessAccountRepository businessAccountRepository, ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository,
                          UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentReceiverRepository = paymentReceiverRepository;
        this.currentAccountRepository = currentAccountRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
        this.foreignCurrencyBalanceRepository = foreignCurrencyBalanceRepository;
        this.userRepository = userRepository;
    }

    public String makePayment(PaymentCreateDto paymentCreateDto) {
        validatePayment(paymentCreateDto.getSenderAccountNumber(),
                        paymentCreateDto.getReceiverAccountNumber(),
                        paymentCreateDto.getAmount());

        subtractAmountFromAccount(paymentCreateDto.getSenderAccountNumber(), paymentCreateDto.getAmount());
        addAmountToAccount(paymentCreateDto.getReceiverAccountNumber(), paymentCreateDto.getAmount());

        return "The payment from account " + paymentCreateDto.getSenderAccountNumber() + " to account " +
                paymentCreateDto.getReceiverAccountNumber() + " has been successfully completed.";
    }

    public String transferMoney(MoneyTransferDto moneyTransferDto) {
        validateMoneyTransfer(moneyTransferDto.getSenderAccountNumber(),
                              moneyTransferDto.getReceiverAccountNumber(),
                              moneyTransferDto.getAmount(),
                              moneyTransferDto.getCurrencySymbol());

        subtractAmountFromAccount(moneyTransferDto.getSenderAccountNumber(), moneyTransferDto.getAmount());
        addAmountToAccount(moneyTransferDto.getReceiverAccountNumber(), moneyTransferDto.getAmount());

        return "The transfer of funds from account " + moneyTransferDto.getSenderAccountNumber() + " to account " +
                moneyTransferDto.getReceiverAccountNumber() + " has been successfully completed.";
    }

    public void subtractAmountFromAccount(String accountNumber, Double amount) {

    }

    public void addAmountToAccount(String accountNumber, Double amount) {

    }

    public void validatePayment(String senderAccountNumber, String receiverAccountNumber, Double amount) {

    }

    public void validateMoneyTransfer(String senderAccountNumber, String receiverAccountNumber, Double amount, String currencySymbol) {

    }

    public PaymentDto findPaymentById(Long id) {
        Optional<Payment> payment = paymentRepository.findById(id);

        return payment.map(PaymentMapper.INSTANCE::paymentToPaymentDto).orElseThrow(() -> new NotFoundException("Payment has not been found."));
    }

    public List<PaymentDto> findAllPaymentsForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        Long userId = user.getId();

        return paymentRepository.findAllBySenderId(userId).stream().map(PaymentMapper.INSTANCE::paymentToPaymentDto).collect(Collectors.toList());
    }

    public List<PaymentDto> findAllPaymentsForAccount(String accountNumber) {

        return paymentRepository.findAllBySenderAccountNumber(accountNumber).stream().map(PaymentMapper.INSTANCE::paymentToPaymentDto).collect(Collectors.toList());
    }

    public PaymentReceiverDto findPaymentReceiverById(Long id) {
        Optional<PaymentReceiver> paymentReceiver = paymentReceiverRepository.findById(id);

        return paymentReceiver.map(PaymentMapper.INSTANCE::paymentReceiverToPaymentReceiverDto).orElseThrow(() -> new NotFoundException("Payment receiver has not been found."));
    }

    public List<PaymentReceiverDto> findAllPaymentReceiversForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        Long userId = user.getId();

        return paymentReceiverRepository.findAllBySenderId(userId).stream().map(PaymentMapper.INSTANCE::paymentReceiverToPaymentReceiverDto).collect(Collectors.toList());
    }

    public PaymentReceiverDto createPaymentReceiver(PaymentReceiverCreateDto paymentReceiverCreateDto){
        PaymentReceiver paymentReceiver = PaymentMapper.INSTANCE.paymentReceiverCreateDtoToPaymentReceiver(paymentReceiverCreateDto);
        paymentReceiverRepository.save(paymentReceiver);

        return PaymentMapper.INSTANCE.paymentReceiverToPaymentReceiverDto(paymentReceiver);
    }

    public PaymentReceiverDto updatePaymentReceiver(PaymentReceiverUpdateDto paymentReceiverUpdateDto, Long id){
        PaymentReceiver paymentReceiver = paymentReceiverRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment receiver has not been found."));

        PaymentMapper.INSTANCE.updatePaymentReceiverFromPaymentReceiverUpdateDto(paymentReceiver, paymentReceiverUpdateDto);
        paymentReceiverRepository.save(paymentReceiver);

        return PaymentMapper.INSTANCE.paymentReceiverToPaymentReceiverDto(paymentReceiver);
    }

    public String deletePaymentReceiver(Long id) {
        PaymentReceiver paymentReceiver = paymentReceiverRepository.findById(id).orElseThrow(() -> new NotFoundException("Payment receiver has not been found."));

        paymentReceiverRepository.delete(paymentReceiver);

        return "Payment receiver has been successfully deleted.";
    }

}
