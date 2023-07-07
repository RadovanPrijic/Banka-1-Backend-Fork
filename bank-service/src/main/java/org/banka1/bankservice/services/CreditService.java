package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.BusinessAccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.credit.CreditDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestDto;
import org.banka1.bankservice.domains.dtos.credit.CreditInstallmentDto;
import org.banka1.bankservice.domains.entities.credit.Credit;
import org.banka1.bankservice.domains.entities.credit.CreditInstallment;
import org.banka1.bankservice.domains.entities.credit.CreditRequest;
import org.banka1.bankservice.domains.entities.credit.CreditRequestStatus;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.CreditMapper;
import org.banka1.bankservice.repositories.CreditRepository;
import org.banka1.bankservice.repositories.CreditRequestRepository;
import org.banka1.bankservice.repositories.CreditInstallmentRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CreditService {

    private final CreditRepository creditRepository;
    private final CreditRequestRepository creditRequestRepository;
    private final CreditInstallmentRepository creditInstallmentRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PaymentService paymentService;

    public CreditService(CreditRepository creditRepository, CreditRequestRepository creditRequestRepository,
                         CreditInstallmentRepository creditInstallmentRepository, UserRepository userRepository,
                         AccountService accountService, PaymentService paymentService) {
        this.creditRepository = creditRepository;
        this.creditRequestRepository = creditRequestRepository;
        this.creditInstallmentRepository = creditInstallmentRepository;
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.paymentService = paymentService;
    }

    public CreditRequestDto createCreditRequest(CreditRequestCreateDto creditRequestCreateDto) {
        validateCreditRequest(creditRequestCreateDto);

        CreditRequest creditRequest = CreditMapper.INSTANCE.creditRequestCreateDtoToCreditRequest(creditRequestCreateDto);
        creditRequest.setClientEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        creditRequest.setCreditRequestStatus(CreditRequestStatus.WAITING);

        creditRequestRepository.save(creditRequest);

        return CreditMapper.INSTANCE.creditRequestToCreditRequestDto(creditRequest);
    }

    public CreditDto approveCreditRequest(Long id) {
        CreditRequest creditRequest = creditRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Credit request has not been found."));

        if(creditRequest.getCreditRequestStatus() != CreditRequestStatus.WAITING)
            throw new ValidationException("The status of this credit request is not WAITING.");

        BankUser user = userRepository.findByEmail(creditRequest.getClientEmail()).orElseThrow(() -> new NotFoundException("User has not been found."));
        AccountDto account = accountService.findAccountByAccountNumber(creditRequest.getAccountNumber());

        Credit credit = Credit.builder()
                .clientId(user.getId())
                .accountNumber(creditRequest.getAccountNumber())
                .creditName(creditRequest.getCreditType().toString() + " credit")
                .creditAmount(creditRequest.getCreditAmount())
                .amortisationLength(creditRequest.getMonthsToPayOff())
                .interestRate(creditRequest.getInterestRate())
                .creditInstallmentAmount((creditRequest.getCreditAmount() * (1.0 + creditRequest.getInterestRate() / 100)) / creditRequest.getMonthsToPayOff())
                .creationDate(LocalDate.now())
                .dueDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(creditRequest.getMonthsToPayOff() + 1), 25))
                .nextInstallmentFirstDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(1), 1))
                .nextInstallmentLastDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(1), 15))
                .leftToPay(creditRequest.getCreditAmount() * (1.0 + creditRequest.getInterestRate() / 100))
                .currencyCode(account.getDefaultCurrencyCode())
                .build();

        creditRequest.setCreditRequestStatus(CreditRequestStatus.APPROVED);
        creditRequestRepository.save(creditRequest);

        creditRepository.save(credit);
        doCreditTransaction(account, creditRequest.getCreditAmount(), "addition");

        return CreditMapper.INSTANCE.creditToCreditDto(credit);
    }

    public CreditRequestDto denyCreditRequest(Long id) {
        CreditRequest creditRequest = creditRequestRepository.findById(id).orElseThrow(() -> new NotFoundException("Credit request has not been found."));

        creditRequest.setCreditRequestStatus(CreditRequestStatus.DENIED);
        creditRequestRepository.save(creditRequest);

        return CreditMapper.INSTANCE.creditRequestToCreditRequestDto(creditRequest);
    }

    public CreditInstallmentDto payCreditInstallment(Long id) {
        Credit credit = creditRepository.findById(id).orElseThrow(() -> new NotFoundException("Credit has not been found."));
        AccountDto account = accountService.findAccountByAccountNumber(credit.getAccountNumber());
        CreditInstallment creditInstallment;

        if(credit.getLeftToPay() == 0.0)
            throw new ValidationException("This credit has already been fully paid off.");

        if(LocalDate.now().isEqual(credit.getNextInstallmentFirstDate()) || LocalDate.now().isAfter(credit.getNextInstallmentFirstDate())
            || LocalDate.now().isBefore(credit.getNextInstallmentLastDate()) || LocalDate.now().isEqual(credit.getNextInstallmentLastDate())) {

            if(credit.getCreditInstallmentAmount() > account.getAccountBalance())
                throw new ValidationException("You don't have enough funds on your bank account to pay this credit installment.");

            doCreditTransaction(account, credit.getCreditInstallmentAmount(), "subtraction");
            credit.setLeftToPay(credit.getLeftToPay() - credit.getCreditInstallmentAmount());

            creditInstallment = CreditInstallment.builder()
                    .creditId(credit.getId())
                    .clientId(credit.getClientId())
                    .creditInstallmentPaymentTime(LocalDateTime.now())
                    .creditInstallmentAmount(credit.getCreditInstallmentAmount())
                    .interestRateAmount(credit.getCreditInstallmentAmount() * (credit.getInterestRate() / 100))
                    .build();

            creditInstallmentRepository.save(creditInstallment);

            if(credit.getNextInstallmentFirstDate().plusMonths(1).isAfter(credit.getDueDate()))
                credit.setLeftToPay(0.0);
            else {
                credit.setNextInstallmentFirstDate(credit.getNextInstallmentFirstDate().plusMonths(1));
                credit.setNextInstallmentLastDate(credit.getNextInstallmentLastDate().plusMonths(1));
            }

            creditRepository.save(credit);

        } else
            throw new ValidationException("Your next credit installment can only be paid between " + credit.getNextInstallmentFirstDate() + " and " + credit.getNextInstallmentLastDate() + ".");

        return CreditMapper.INSTANCE.creditInstallmentToCreditInstallmentDto(creditInstallment);
    }

    public CreditRequestDto findCreditRequestById(Long id) {
        Optional<CreditRequest> creditRequest = creditRequestRepository.findById(id);

        return creditRequest.map(CreditMapper.INSTANCE::creditRequestToCreditRequestDto).orElseThrow(() -> new NotFoundException("Credit request has not been found."));
    }

    public List<CreditRequestDto> findAllWaitingCreditRequests() {

        return creditRequestRepository.findAllByCreditRequestStatus(CreditRequestStatus.WAITING).stream().map(CreditMapper.INSTANCE::creditRequestToCreditRequestDto).collect(Collectors.toList());
    }

    public List<CreditRequestDto> findAllCreditRequestsForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return creditRequestRepository.findAllByClientEmail(email).stream().map(CreditMapper.INSTANCE::creditRequestToCreditRequestDto).collect(Collectors.toList());
    }

    public List<CreditRequestDto> findAllCreditRequestsForAccount(String accountNumber) {

        return creditRequestRepository.findAllByAccountNumber(accountNumber).stream().map(CreditMapper.INSTANCE::creditRequestToCreditRequestDto).collect(Collectors.toList());
    }

    public CreditDto findCreditById(Long id) {
        Optional<Credit> credit = creditRepository.findById(id);

        return credit.map(CreditMapper.INSTANCE::creditToCreditDto).orElseThrow(() -> new NotFoundException("Credit has not been found."));
    }

    public List<CreditDto> findAllCreditsForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        Long userId = user.getId();

        return creditRepository.findAllByClientIdOrderByCreditAmountDesc(userId).stream().map(CreditMapper.INSTANCE::creditToCreditDto).collect(Collectors.toList());
    }

    public List<CreditDto> findAllCreditsForAccount(String accountNumber) {

        return creditRepository.findAllByAccountNumberOrderByCreditAmountDesc(accountNumber).stream().map(CreditMapper.INSTANCE::creditToCreditDto).collect(Collectors.toList());
    }

    public List<CreditInstallmentDto> findAllCreditInstallmentsForCredit(Long id) {

        return creditInstallmentRepository.findAllByCreditId(id).stream().map(CreditMapper.INSTANCE::creditInstallmentToCreditInstallmentDto).collect(Collectors.toList());
    }

    public void validateCreditRequest(CreditRequestCreateDto creditRequestCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));

        boolean accountFoundInUserAccounts = false;
        List<AccountDto> userAccounts = accountService.findAllAccountsForUserById(user.getId());
        for(AccountDto account : userAccounts ) {
            if (account.getAccountNumber().equals(creditRequestCreateDto.getAccountNumber())) {
                accountFoundInUserAccounts = true;
                break;
            }
        }

        if(!accountFoundInUserAccounts)
            throw new ValidationException("This account does not belong to the user specified in credit request creation form.");

        List<CreditRequest> userCreditRequests = creditRequestRepository.findAllByClientEmailAndCreditRequestStatus(email, CreditRequestStatus.WAITING);
        if(userCreditRequests.size() == 5)
            throw new ValidationException("User has already reached a maximum of 5 waiting credit requests per user.");
    }

    public void doCreditTransaction(AccountDto creditAccount, Double amount, String operation) {
        if(creditAccount instanceof CurrentAccountDto)
            paymentService.changeAccountBalance("CURRENT", creditAccount.getAccountNumber(), amount, operation, creditAccount.getDefaultCurrencyCode());
        else if(creditAccount instanceof ForeignCurrencyAccountDto)
            paymentService.changeAccountBalance("FOREIGN_CURRENCY", creditAccount.getAccountNumber(), amount, operation, creditAccount.getDefaultCurrencyCode());
        else if(creditAccount instanceof BusinessAccountDto)
            paymentService.changeAccountBalance("BUSINESS", creditAccount.getAccountNumber(), amount, operation, creditAccount.getDefaultCurrencyCode());
    }

}
