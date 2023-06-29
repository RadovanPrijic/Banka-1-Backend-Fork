package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.banka1.bankservice.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void makePayment(PaymentCreateDto paymentCreateDto) {

    }

    public void transferMoney(MoneyTransferDto moneyTransferDto) {

    }

    public void subtractAmountFromAccount(String accountNumber, Double amount) {

    }

    public void addAmountToAccount(String accountNumber, Double amount) {

    }

    public void validatePayment(String senderAccountNumber, String receiverAccountNumber, Double amount) {

    }

    public void validateTransfer(String senderAccountNumber, String receiverAccountNumber, Double amount, String currencySymbol) {

    }

    public PaymentDto getPaymentById(Long id) {

        return null;
    }

    public List<PaymentDto> getAllPaymentsForLoggedInUser() {

        return null;
    }

    public List<PaymentDto> getAllPaymentsForAccount(String accountNumber) {

        return null;
    }

    public PaymentReceiverDto getPaymentReceiverById(Long id) {

        return null;
    }

    public List<PaymentReceiverDto> getAllPaymentReceivers() {

        return null;
    }

    public PaymentReceiverDto createPaymentReceiver(PaymentReceiverCreateDto paymentReceiverCreateDto){

        return null;
    }

    public PaymentReceiverDto updatePaymentReceiver(PaymentReceiverUpdateDto paymentReceiverUpdateDto){

        return null;
    }

    public void deletePaymentReceiver(Long id) {
//        UserContract userContract = userContractRepository.findByContractId(contractId);
//        BankAccount bankAccount = bankAccountRepository.findAll().get(0);
//        bankAccount.setReservedAsset(bankAccount.getReservedAsset() - userContract.getPrice());
//        bankAccountRepository.save(bankAccount);
//        userContractRepository.delete(userContract);
    }

}
