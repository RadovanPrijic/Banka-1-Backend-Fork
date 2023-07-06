package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.account.*;
import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.domains.entities.account.*;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.PaymentMapper;
import org.banka1.bankservice.repositories.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private final AccountService accountService;

    public PaymentService(PaymentRepository paymentRepository, PaymentReceiverRepository paymentReceiverRepository,
                          CurrentAccountRepository currentAccountRepository, ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                          BusinessAccountRepository businessAccountRepository, ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository,
                          UserRepository userRepository, AccountService accountService) {
        this.paymentRepository = paymentRepository;
        this.paymentReceiverRepository = paymentReceiverRepository;
        this.currentAccountRepository = currentAccountRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
        this.foreignCurrencyBalanceRepository = foreignCurrencyBalanceRepository;
        this.userRepository = userRepository;
        this.accountService = accountService;
    }

    public PaymentDto makePayment(PaymentCreateDto paymentCreateDto) {
        String[] accountTypes = validatePayment(paymentCreateDto.getSenderAccountNumber(),
                                                paymentCreateDto.getReceiverAccountNumber(),
                                                paymentCreateDto.getAmount());

        changeAccountBalance(accountTypes[0],
                             paymentCreateDto.getSenderAccountNumber(),
                             paymentCreateDto.getAmount(),
                             "subtraction",
                             "RSD");

        changeAccountBalance(accountTypes[1],
                             paymentCreateDto.getReceiverAccountNumber(),
                             paymentCreateDto.getAmount(),
                             "addition",
                             "RSD");

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<BankUser> user = userRepository.findByEmail(email);
        Long userId = user.get().getId();

        Payment payment = PaymentMapper.INSTANCE.paymentCreateDtoToPayment(paymentCreateDto);
        payment.setSenderId(userId);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setCurrencySymbol("RSD");
        paymentRepository.save(payment);

        return PaymentMapper.INSTANCE.paymentToPaymentDto(payment);
    }

    public PaymentDto transferMoney(MoneyTransferDto moneyTransferDto) {
        String[] accountTypes = validateMoneyTransfer(moneyTransferDto.getSenderAccountNumber(),
                                                      moneyTransferDto.getReceiverAccountNumber(),
                                                      moneyTransferDto.getAmount(),
                                                      moneyTransferDto.getCurrencySymbol());

        changeAccountBalance(accountTypes[0],
                             moneyTransferDto.getSenderAccountNumber(),
                             moneyTransferDto.getAmount(),
                             "subtraction",
                             moneyTransferDto.getCurrencySymbol());

        changeAccountBalance(accountTypes[1],
                             moneyTransferDto.getReceiverAccountNumber(),
                             moneyTransferDto.getAmount(),
                             "addition",
                             moneyTransferDto.getCurrencySymbol());

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<BankUser> user = userRepository.findByEmail(email);
        Long userId = user.get().getId();

        Payment payment = PaymentMapper.INSTANCE.moneyTransferDtoToPayment(moneyTransferDto);
        payment.setSenderId(userId);
        payment.setPaymentTime(LocalDateTime.now());
        payment.setPaymentPurpose("Interni prenos u " + moneyTransferDto.getCurrencySymbol() + " valuti");
        paymentRepository.save(payment);

        return PaymentMapper.INSTANCE.paymentToPaymentDto(payment);
    }

    public void changeAccountBalance(String accountType, String accountNumber, Double amount, String operation, String currencySymbol) {

        switch (accountType) {

            case "CURRENT" -> {
                CurrentAccount currentAccount = currentAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new NotFoundException("Current account has not been found."));

                if(operation.equals("subtraction"))
                    currentAccount.setAccountBalance(currentAccount.getAccountBalance() - amount);
                else if(operation.equals("addition"))
                    currentAccount.setAccountBalance(currentAccount.getAccountBalance() + amount);

                currentAccountRepository.save(currentAccount);
            }

            case "FOREIGN_CURRENCY" -> {
                ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new NotFoundException("Foreign currency account has not been found."));
                List<ForeignCurrencyBalance> balances = foreignCurrencyAccount.getForeignCurrencyBalances();
                ForeignCurrencyBalance foreignCurrencyBalance = null;

                for(ForeignCurrencyBalance balance : balances) {
                    if(balance.getForeignCurrencyCode().equals(currencySymbol)){
                        foreignCurrencyBalance = balance;
                        break;
                    }
                }

                balances.remove(foreignCurrencyBalance);

                if(operation.equals("subtraction"))
                    foreignCurrencyBalance.setAccountBalance(foreignCurrencyBalance.getAccountBalance() - amount);
                else if(operation.equals("addition"))
                    foreignCurrencyBalance.setAccountBalance(foreignCurrencyBalance.getAccountBalance() + amount);

                foreignCurrencyBalanceRepository.saveAndFlush(foreignCurrencyBalance);

                balances.add(foreignCurrencyBalance);
                foreignCurrencyAccount.setForeignCurrencyBalances(balances);
                foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
            }

            case "BUSINESS" -> {
                BusinessAccount businessAccount = businessAccountRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new NotFoundException("Business account has not been found."));

                if(operation.equals("subtraction"))
                    businessAccount.setAccountBalance(businessAccount.getAccountBalance() - amount);
                else if(operation.equals("addition"))
                    businessAccount.setAccountBalance(businessAccount.getAccountBalance() + amount);

                businessAccountRepository.save(businessAccount);
            }

        }
    }

    public String[] validatePayment(String senderAccountNumber, String receiverAccountNumber, Double amount) {
        List<AccountDto> userAccounts = accountService.findAllAccountsForLoggedInUser();
        AccountDto senderAccount = null, receiverAccount;

        for(AccountDto account : userAccounts){
            if(account.getAccountNumber().equals(senderAccountNumber))
                senderAccount = account;
        }

        receiverAccount = accountService.findAccountByAccountNumber(receiverAccountNumber);

        if(senderAccount == null)
            throw new NotFoundException("Sender account has not been found among user's accounts.");
        if(receiverAccount == null)
            throw new NotFoundException("Receiver account has not been found.");

        return validateFurtherAndReturnTypes(senderAccount, receiverAccount, amount, false, "RSD", null);
    }

    public String[] validateMoneyTransfer(String senderAccountNumber, String receiverAccountNumber, Double amount, String currencySymbol) {
        List<AccountDto> userAccounts = accountService.findAllAccountsForLoggedInUser();
        AccountDto senderAccount = null, receiverAccount = null;

        for(AccountDto account : userAccounts){
            if(account.getAccountNumber().equals(senderAccountNumber))
                senderAccount = account;
            else if (account.getAccountNumber().equals(receiverAccountNumber))
                receiverAccount = account;
        }

        if(senderAccount == null)
            throw new NotFoundException("Sender account has not been found among user's accounts.");
        if(receiverAccount == null)
            throw new NotFoundException("Receiver account has not been found among user's accounts.");

        return validateFurtherAndReturnTypes(senderAccount, receiverAccount, amount, false, currencySymbol, null);
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

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        paymentReceiver.setSenderId(user.getId());

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

    public String[] validateFurtherAndReturnTypes(AccountDto senderAccount, AccountDto receiverAccount, Double amount,
                                                  boolean conversion, String currencySymbolOne, String currencySymbolTwo){
        String[] accountTypes = new String[2];
        Double accountBalance = 0.0;

        if(senderAccount instanceof CurrentAccountDto) {
            accountTypes[0] = "CURRENT";
            accountBalance = senderAccount.getAccountBalance();
        } else if(senderAccount instanceof ForeignCurrencyAccountDto) {
            accountTypes[0] = "FOREIGN_CURRENCY";
            accountBalance = validateCurrencyOnForeignCurrencyAccount(senderAccount, currencySymbolOne);
        } else if(senderAccount instanceof BusinessAccountDto) {
            accountTypes[0] = "BUSINESS";
            accountBalance = senderAccount.getAccountBalance();
        }

        if(accountBalance < amount)
            throw new ValidationException("The amount of funds on the sender account is not high enough to successfully complete the transaction.");

        if(receiverAccount instanceof CurrentAccountDto)
            accountTypes[1] = "CURRENT";
        else if(receiverAccount instanceof ForeignCurrencyAccountDto) {
            accountTypes[1] = "FOREIGN_CURRENCY";

            if(conversion)
                validateCurrencyOnForeignCurrencyAccount(receiverAccount, currencySymbolTwo);
            else
                validateCurrencyOnForeignCurrencyAccount(receiverAccount, currencySymbolOne);

        } else if(receiverAccount instanceof BusinessAccountDto)
            accountTypes[1] = "BUSINESS";

        return accountTypes;
    }

    public Double validateCurrencyOnForeignCurrencyAccount(AccountDto account, String currencySymbol) {
        boolean currencyPresentOnAccount = false;
        Double accountBalance = 0.0;
        List<ForeignCurrencyBalanceDto> accountBalances = ((ForeignCurrencyAccountDto) account).getForeignCurrencyBalances();

        for(ForeignCurrencyBalanceDto balance : accountBalances) {
            if (balance.getForeignCurrencyCode().equals(currencySymbol)) {
                currencyPresentOnAccount = true;
                accountBalance = balance.getAccountBalance();
                break;
            }
        }

        if(!currencyPresentOnAccount)
            throw new ValidationException("Currency " + currencySymbol + " is not present on foreign currency account.");

        return accountBalance;
    }

}
