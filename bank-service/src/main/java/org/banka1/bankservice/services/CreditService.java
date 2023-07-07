package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.BusinessAccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.credit.CreditDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestDto;
import org.banka1.bankservice.domains.dtos.credit.InterestRatePaymentDto;
import org.banka1.bankservice.domains.entities.credit.Credit;
import org.banka1.bankservice.domains.entities.credit.CreditRequest;
import org.banka1.bankservice.domains.entities.credit.CreditRequestStatus;
import org.banka1.bankservice.domains.entities.credit.InterestRatePayment;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.CreditMapper;
import org.banka1.bankservice.repositories.CreditRepository;
import org.banka1.bankservice.repositories.CreditRequestRepository;
import org.banka1.bankservice.repositories.InterestRatePaymentRepository;
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
    private final InterestRatePaymentRepository interestRatePaymentRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PaymentService paymentService;

    public CreditService(CreditRepository creditRepository, CreditRequestRepository creditRequestRepository,
                         InterestRatePaymentRepository interestRatePaymentRepository, UserRepository userRepository,
                         AccountService accountService, PaymentService paymentService) {
        this.creditRepository = creditRepository;
        this.creditRequestRepository = creditRequestRepository;
        this.interestRatePaymentRepository = interestRatePaymentRepository;
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

        creditRequest.setCreditRequestStatus(CreditRequestStatus.APPROVED);
        creditRequestRepository.save(creditRequest);

        BankUser user = userRepository.findByEmail(creditRequest.getClientEmail()).orElseThrow(() -> new NotFoundException("User has not been found."));
        AccountDto account = accountService.findAccountByAccountNumber(creditRequest.getAccountNumber());

        Credit credit = Credit.builder()
                .clientId(user.getId())
                .accountNumber(creditRequest.getAccountNumber())
                .creditName(creditRequest.getCreditType().toString() + " credit")
                .creditAmount(creditRequest.getCreditAmount())
                .amortisationLength(creditRequest.getMonthsToPayOff())
                .interestRate(creditRequest.getInterestRate())
                .rateAmount((creditRequest.getCreditAmount() * (creditRequest.getInterestRate() / 100)) / creditRequest.getMonthsToPayOff())
                .creationDate(LocalDate.now())
                .dueDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(creditRequest.getMonthsToPayOff() + 1), 25))
                .nextRateFirstDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(1), 1))
                .nextRateLastDate(LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth().plus(1), 15))
                .leftToPay(creditRequest.getCreditAmount() * (creditRequest.getInterestRate() / 100))
                .currencyCode(account.getDefaultCurrencyCode())
                .build();

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

    public InterestRatePaymentDto payCreditInstallment(Long id) {
        Credit credit = creditRepository.findById(id).orElseThrow(() -> new NotFoundException("Credit has not been found."));
        AccountDto account = accountService.findAccountByAccountNumber(credit.getAccountNumber());
        InterestRatePayment interestRatePayment;

        if(credit.getLeftToPay() == 0.0)
            throw new ValidationException("This credit has been fully paid off.");

        if(LocalDate.now().isEqual(credit.getNextRateFirstDate()) || LocalDate.now().isAfter(credit.getNextRateFirstDate())
            || LocalDate.now().isBefore(credit.getNextRateLastDate()) || LocalDate.now().isEqual(credit.getNextRateLastDate())) {

            if(credit.getRateAmount() > account.getAccountBalance())
                throw new ValidationException("You don't have enough funds on your bank account to pay this credit installment.");

            doCreditTransaction(account, credit.getRateAmount(), "subtraction");
            credit.setLeftToPay(credit.getLeftToPay() - credit.getRateAmount());

            interestRatePayment = InterestRatePayment.builder()
                    .creditId(credit.getId())
                    .clientId(credit.getClientId())
                    .interestRatePaymentTime(LocalDateTime.now())
                    .paymentAmount(credit.getRateAmount())
                    .interestRateAmount(credit.getRateAmount() * (credit.getInterestRate() / 100))
                    .build();

            interestRatePaymentRepository.save(interestRatePayment);

            if(credit.getNextRateFirstDate().plusMonths(1).isAfter(credit.getDueDate()))
                credit.setLeftToPay(0.0);
            else {
                credit.setNextRateFirstDate(credit.getNextRateFirstDate().plusMonths(1));
                credit.setNextRateLastDate(credit.getNextRateLastDate().plusMonths(1));
            }

            creditRepository.save(credit);

        } else
            throw new ValidationException("Your next rate must be paid between " + credit.getNextRateFirstDate() + " and " + credit.getNextRateLastDate() + ".");

        return CreditMapper.INSTANCE.interestRateToInterestRatePaymentDto(interestRatePayment);
    }

    /*
     public CommunicationDto payOffOneMonthsInterest(String creditId) {

        double monthlyRate = credit.get().getMonthlyRate();

        increaseOrDecreaseUserBalance(monthlyRate, credit.get().getAccountRegNumber(), true);
        credit.get().setRemainingAmount(credit.get().getRemainingAmount() - monthlyRate);
        creditRepository.save(credit.get());
        payedInterestRepository.save(new PayedInterest("Kamata", creditId, dtf.format(LocalDate.now()), monthlyRate));

        return new CommunicationDto(200, "Mesecna kamata je placena");
    }
    */

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

    public List<InterestRatePaymentDto> findAllInterestRatePaymentsForCredit(Long id) {

        return interestRatePaymentRepository.findAllByCreditId(id).stream().map(CreditMapper.INSTANCE::interestRateToInterestRatePaymentDto).collect(Collectors.toList());
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
        if(userCreditRequests.size() == 4)
            throw new ValidationException("User has already reached a maximum of 4 waiting credit requests per user.");
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
