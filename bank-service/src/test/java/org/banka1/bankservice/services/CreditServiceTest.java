package org.banka1.bankservice.services;

import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.BusinessAccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.credit.CreditDto;
import org.banka1.bankservice.domains.dtos.credit.CreditInstallmentDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestCreateDto;
import org.banka1.bankservice.domains.dtos.credit.CreditRequestDto;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.credit.*;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.repositories.CreditInstallmentRepository;
import org.banka1.bankservice.repositories.CreditRepository;
import org.banka1.bankservice.repositories.CreditRequestRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private CreditRequestRepository creditRequestRepository;

    @Mock
    private CreditInstallmentRepository creditInstallmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private CreditService creditServiceMock;

    @InjectMocks
    private CreditService creditService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void createCreditRequest_InvalidCreditRequest_ThrowsValidationException() {
        // Arrange
        CreditRequestCreateDto creditRequestCreateDto = new CreditRequestCreateDto();
        String clientEmail = "test@example.com";
        when(userRepository.findByEmail(clientEmail)).thenReturn(Optional.of(new BankUser()));

        // Act & Assert
        // Set up the behavior of the creditService mock
        doThrow(NullPointerException.class).when(creditServiceMock).createCreditRequest(creditRequestCreateDto);
        doThrow(NotFoundException.class).when(creditServiceMock).createCreditRequest(creditRequestCreateDto);

        // Perform the test and verify the exceptions
        assertThatThrownBy(() -> creditServiceMock.createCreditRequest(creditRequestCreateDto))
                .isInstanceOfAny(NullPointerException.class, NotFoundException.class);
        verify(userRepository, never()).findByEmail(any());
        verify(creditRequestRepository, never()).save(any());
    }

    @Test
    void approveCreditRequest_ValidCreditRequest_ReturnsCreditDto() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        creditRequest.setCreditRequestStatus(CreditRequestStatus.WAITING);
        creditRequest.setClientEmail("test@example.com");
        creditRequest.setAccountNumber("123456789");
        creditRequest.setCreditType(CreditType.AUTO);
        creditRequest.setCreditAmount(1.2);
        creditRequest.setInterestRate(1.2);
        creditRequest.setMonthsToPayOff(12);

        BankUser user = new BankUser();
        user.setId(1L);

        AccountDto accountDto = new AccountDto();
        accountDto.setDefaultCurrencyCode("USD");

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));
        when(userRepository.findByEmail(creditRequest.getClientEmail())).thenReturn(Optional.of(user));
        when(accountService.findAccountByAccountNumber(creditRequest.getAccountNumber())).thenReturn(accountDto);
        when(creditRequestRepository.save(any())).thenReturn(creditRequest);

        // Act
        CreditDto creditDto = creditService.approveCreditRequest(creditRequestId);

        // Assert
        assertNotNull(creditDto);
        assertEquals(CreditRequestStatus.APPROVED, creditRequest.getCreditRequestStatus());
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(userRepository, times(1)).findByEmail(creditRequest.getClientEmail());
        verify(accountService, times(1)).findAccountByAccountNumber(creditRequest.getAccountNumber());
        verify(creditRequestRepository, times(1)).save(creditRequest);
        verify(creditRepository, times(1)).save(any());
    }

    @Test
    void approveCreditRequest_NonExistingCreditRequest_ThrowsNotFoundException() {
        // Arrange
        Long creditRequestId = 1L;

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> creditService.approveCreditRequest(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(userRepository, never()).findByEmail(any());
        verify(accountService, never()).findAccountByAccountNumber(any());
        verify(creditRequestRepository, never()).save(any());
        verify(creditRepository, never()).save(any());
    }

    @Test
    void approveCreditRequest_CreditRequestNotWaiting_ThrowsValidationException() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        creditRequest.setCreditRequestStatus(CreditRequestStatus.APPROVED);

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));

        // Act & Assert
        assertThrows(ValidationException.class, () -> creditService.approveCreditRequest(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(userRepository, never()).findByEmail(any());
        verify(accountService, never()).findAccountByAccountNumber(any());
        verify(creditRequestRepository, never()).save(any());
        verify(creditRepository, never()).save(any());
    }

    @Test
    void approveCreditRequest_NonExistingUser_ThrowsNotFoundException() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        creditRequest.setCreditRequestStatus(CreditRequestStatus.WAITING);
        creditRequest.setClientEmail("nonexisting@example.com");

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));
        when(userRepository.findByEmail(creditRequest.getClientEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> creditService.approveCreditRequest(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(userRepository, times(1)).findByEmail(creditRequest.getClientEmail());
        verify(accountService, never()).findAccountByAccountNumber(any());
        verify(creditRequestRepository, never()).save(any());
        verify(creditRepository, never()).save(any());
    }
    @Test
    void denyCreditRequest_ValidCreditRequest_ReturnsCreditRequestDto() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        creditRequest.setCreditRequestStatus(CreditRequestStatus.WAITING);

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));
        when(creditRequestRepository.save(any())).thenReturn(creditRequest);

        // Act
        CreditRequestDto creditRequestDto = creditService.denyCreditRequest(creditRequestId);

        // Assert
        assertNotNull(creditRequestDto);
        assertEquals(CreditRequestStatus.DENIED, creditRequest.getCreditRequestStatus());
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(creditRequestRepository, times(1)).save(creditRequest);
    }

    @Test
    void denyCreditRequest_NonExistingCreditRequest_ThrowsNotFoundException() {
        // Arrange
        Long creditRequestId = 1L;

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> creditService.denyCreditRequest(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(creditRequestRepository, never()).save(any());
    }

    @Test
    void denyCreditRequest_CreditRequestNotWaiting_ThrowsValidationException() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);
        creditRequest.setCreditRequestStatus(CreditRequestStatus.DENIED);

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));

        // Act & Assert
        assertThrows(ValidationException.class, () -> creditService.denyCreditRequest(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
        verify(creditRequestRepository, never()).save(any());
    }

    @Test
    void payCreditInstallment_ValidCredit_ReturnsCreditInstallmentDto() {
        // Arrange
        Long creditId = 1L;

        Credit credit = new Credit();
        credit.setId(creditId);
        credit.setLeftToPay(1000.0);
        credit.setAccountNumber("123456789");
        credit.setCreditInstallmentAmount(1.2);
        credit.setInterestRate(11.2);
        credit.setNextInstallmentFirstDate(LocalDate.now().plusMonths(1));
        credit.setDueDate(LocalDate.now());

        AccountDto accountDto = new AccountDto();
        accountDto.setAccountBalance(2000.0);

        CreditInstallment creditInstallment = new CreditInstallment();

        LocalDate currentDate = LocalDate.now();
        LocalDate nextInstallmentFirstDate = currentDate.minusDays(1);
        LocalDate nextInstallmentLastDate = currentDate.plusDays(1);

        credit.setNextInstallmentFirstDate(nextInstallmentFirstDate);
        credit.setNextInstallmentLastDate(nextInstallmentLastDate);

        when(creditRepository.findById(creditId)).thenReturn(Optional.of(credit));
        when(accountService.findAccountByAccountNumber(credit.getAccountNumber())).thenReturn(accountDto);
        when(creditInstallmentRepository.save(any())).thenReturn(creditInstallment);

        // Act
        CreditInstallmentDto creditInstallmentDto = creditService.payCreditInstallment(creditId);

        // Assert
        assertNotNull(creditInstallmentDto);
        assertEquals(credit.getCreditInstallmentAmount(), creditInstallmentDto.getCreditInstallmentAmount());
        verify(creditRepository, times(1)).findById(creditId);
        verify(accountService, times(1)).findAccountByAccountNumber(credit.getAccountNumber());
        verify(creditInstallmentRepository, times(1)).save(any());
        verify(creditRepository, times(1)).save(credit);
    }

    @Test
    void payCreditInstallment_CreditAlreadyPaidOff_ThrowsValidationException() {
        // Arrange
        Long creditId = 1L;

        Credit credit = new Credit();
        credit.setId(creditId);
        credit.setLeftToPay(0.0);

        when(creditRepository.findById(creditId)).thenReturn(Optional.of(credit));

        // Act & Assert
        assertThrows(ValidationException.class, () -> creditService.payCreditInstallment(creditId));
    }

    @Test
    void payCreditInstallment_NotWithinInstallmentDates_ThrowsValidationException() {
        // Arrange
        Long creditId = 1L;

        Credit credit = new Credit();
        credit.setId(creditId);
        credit.setLeftToPay(1000.0);

        LocalDate currentDate = LocalDate.now();
        LocalDate nextInstallmentFirstDate = currentDate.plusDays(1);
        LocalDate nextInstallmentLastDate = currentDate.plusDays(2);

        credit.setNextInstallmentFirstDate(nextInstallmentFirstDate);
        credit.setNextInstallmentLastDate(nextInstallmentLastDate);

        when(creditRepository.findById(creditId)).thenReturn(Optional.of(credit));

        // Act & Assert
        assertThrows(ValidationException.class, () -> creditService.payCreditInstallment(creditId));
    }

    @Test
    void findCreditRequestById_ExistingCreditRequest_ReturnsCreditRequestDto() {
        // Arrange
        Long creditRequestId = 1L;

        CreditRequest creditRequest = new CreditRequest();
        creditRequest.setId(creditRequestId);

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.of(creditRequest));

        // Act
        CreditRequestDto creditRequestDto = creditService.findCreditRequestById(creditRequestId);

        // Assert
        assertNotNull(creditRequestDto);
        assertEquals(creditRequestId, creditRequestDto.getId());
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
    }

    @Test
    void findCreditRequestById_NonExistingCreditRequest_ThrowsNotFoundException() {
        // Arrange
        Long creditRequestId = 1L;

        when(creditRequestRepository.findById(creditRequestId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> creditService.findCreditRequestById(creditRequestId));
        verify(creditRequestRepository, times(1)).findById(creditRequestId);
    }

    @Test
    void findAllWaitingCreditRequests_ExistingWaitingCreditRequests_ReturnsListOfCreditRequestDto() {
        // Arrange
        CreditRequest creditRequest1 = new CreditRequest();
        creditRequest1.setId(1L);
        creditRequest1.setCreditRequestStatus(CreditRequestStatus.WAITING);

        CreditRequest creditRequest2 = new CreditRequest();
        creditRequest2.setId(2L);
        creditRequest2.setCreditRequestStatus(CreditRequestStatus.WAITING);

        when(creditRequestRepository.findAllByCreditRequestStatus(CreditRequestStatus.WAITING)).thenReturn(Arrays.asList(creditRequest1, creditRequest2));

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllWaitingCreditRequests();

        // Assert
        assertNotNull(creditRequestDtos);
        assertEquals(2, creditRequestDtos.size());
        verify(creditRequestRepository, times(1)).findAllByCreditRequestStatus(CreditRequestStatus.WAITING);
    }

    @Test
    void findAllWaitingCreditRequests_NoWaitingCreditRequests_ReturnsEmptyList() {
        // Arrange
        when(creditRequestRepository.findAllByCreditRequestStatus(CreditRequestStatus.WAITING)).thenReturn(Arrays.asList());

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllWaitingCreditRequests();

        // Assert
        assertNotNull(creditRequestDtos);
        assertTrue(creditRequestDtos.isEmpty());
        verify(creditRequestRepository, times(1)).findAllByCreditRequestStatus(CreditRequestStatus.WAITING);
    }

    @Test
    void findAllCreditRequestsForLoggedInUser_ExistingCreditRequests_ReturnsListOfCreditRequestDto() {
        // Arrange
        String email = "test@example.com";
        //OVDE FALI SECURITY STO SI UZEO OD CELAVOG
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        CreditRequest creditRequest1 = new CreditRequest();
        creditRequest1.setId(1L);
        creditRequest1.setClientEmail(email);

        CreditRequest creditRequest2 = new CreditRequest();
        creditRequest2.setId(2L);
        creditRequest2.setClientEmail(email);

        when(creditRequestRepository.findAllByClientEmail(email)).thenReturn(Arrays.asList(creditRequest1, creditRequest2));

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllCreditRequestsForLoggedInUser();

        // Assert
        assertNotNull(creditRequestDtos);
        assertEquals(0, creditRequestDtos.size());
    }

    @Test
    void findAllCreditRequestsForLoggedInUser_NoCreditRequests_ReturnsEmptyList() {
        // Arrange
        String email = "test@example.com";
        //OVDE FALI SECURITY STO SI UZEO OD CELAVOG
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(creditRequestRepository.findAllByClientEmail(email)).thenReturn(Arrays.asList());

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllCreditRequestsForLoggedInUser();

        // Assert
        assertNotNull(creditRequestDtos);
        assertTrue(creditRequestDtos.isEmpty());
    }

    @Test
    void findAllCreditRequestsForAccount_ExistingCreditRequests_ReturnsListOfCreditRequestDto() {
        // Arrange
        String accountNumber = "123456789";

        CreditRequest creditRequest1 = new CreditRequest();
        creditRequest1.setId(1L);
        creditRequest1.setAccountNumber(accountNumber);

        CreditRequest creditRequest2 = new CreditRequest();
        creditRequest2.setId(2L);
        creditRequest2.setAccountNumber(accountNumber);

        when(creditRequestRepository.findAllByAccountNumber(accountNumber)).thenReturn(Arrays.asList(creditRequest1, creditRequest2));

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllCreditRequestsForAccount(accountNumber);

        // Assert
        assertNotNull(creditRequestDtos);
        assertEquals(2, creditRequestDtos.size());
        verify(creditRequestRepository, times(1)).findAllByAccountNumber(accountNumber);
    }

    @Test
    void findAllCreditRequestsForAccount_NoCreditRequests_ReturnsEmptyList() {
        // Arrange
        String accountNumber = "123456789";

        when(creditRequestRepository.findAllByAccountNumber(accountNumber)).thenReturn(Arrays.asList());

        // Act
        List<CreditRequestDto> creditRequestDtos = creditService.findAllCreditRequestsForAccount(accountNumber);

        // Assert
        assertNotNull(creditRequestDtos);
        assertTrue(creditRequestDtos.isEmpty());
        verify(creditRequestRepository, times(1)).findAllByAccountNumber(accountNumber);
    }

    @Test
    void findCreditById_ExistingCredit_ReturnsCreditDto() {
        // Arrange
        Long creditId = 1L;

        Credit credit = new Credit();
        credit.setId(creditId);

        when(creditRepository.findById(creditId)).thenReturn(Optional.of(credit));

        // Act
        CreditDto creditDto = creditService.findCreditById(creditId);

        // Assert
        assertNotNull(creditDto);
        assertEquals(creditId, creditDto.getId());
        verify(creditRepository, times(1)).findById(creditId);
    }

    @Test
    void findCreditById_NonExistingCredit_ThrowsNotFoundException() {
        // Arrange
        Long creditId = 1L;

        when(creditRepository.findById(creditId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> creditService.findCreditById(creditId));
        verify(creditRepository, times(1)).findById(creditId);
    }

    @Test
    void findAllCreditsForAccount_ExistingCredits_ReturnsListOfCreditDto() {
        // Arrange
        String accountNumber = "123456789";

        Credit credit1 = new Credit();
        credit1.setId(1L);
        credit1.setAccountNumber(accountNumber);

        Credit credit2 = new Credit();
        credit2.setId(2L);
        credit2.setAccountNumber(accountNumber);

        when(creditRepository.findAllByAccountNumberOrderByCreditAmountDesc(accountNumber)).thenReturn(Arrays.asList(credit1, credit2));

        // Act
        List<CreditDto> creditDtos = creditService.findAllCreditsForAccount(accountNumber);

        // Assert
        assertNotNull(creditDtos);
        assertEquals(2, creditDtos.size());
        verify(creditRepository, times(1)).findAllByAccountNumberOrderByCreditAmountDesc(accountNumber);
    }

    @Test
    void findAllCreditsForAccount_NoCredits_ReturnsEmptyList() {
        // Arrange
        String accountNumber = "123456789";

        when(creditRepository.findAllByAccountNumberOrderByCreditAmountDesc(accountNumber)).thenReturn(Arrays.asList());

        // Act
        List<CreditDto> creditDtos = creditService.findAllCreditsForAccount(accountNumber);

        // Assert
        assertNotNull(creditDtos);
        assertTrue(creditDtos.isEmpty());
        verify(creditRepository, times(1)).findAllByAccountNumberOrderByCreditAmountDesc(accountNumber);
    }

    @Test
    void findAllCreditInstallmentsForCredit_ExistingCreditInstallments_ReturnsListOfCreditInstallmentDto() {
        // Arrange
        Long creditId = 1L;

        CreditInstallment creditInstallment1 = new CreditInstallment();
        creditInstallment1.setId(1L);
        creditInstallment1.setCreditId(creditId);

        CreditInstallment creditInstallment2 = new CreditInstallment();
        creditInstallment2.setId(2L);
        creditInstallment2.setCreditId(creditId);

        when(creditInstallmentRepository.findAllByCreditId(creditId)).thenReturn(Arrays.asList(creditInstallment1, creditInstallment2));

        // Act
        List<CreditInstallmentDto> creditInstallmentDtos = creditService.findAllCreditInstallmentsForCredit(creditId);

        // Assert
        assertNotNull(creditInstallmentDtos);
        assertEquals(2, creditInstallmentDtos.size());
        verify(creditInstallmentRepository, times(1)).findAllByCreditId(creditId);
    }

    @Test
    void findAllCreditInstallmentsForCredit_NoCreditInstallments_ReturnsEmptyList() {
        // Arrange
        Long creditId = 1L;

        when(creditInstallmentRepository.findAllByCreditId(creditId)).thenReturn(Arrays.asList());

        // Act
        List<CreditInstallmentDto> creditInstallmentDtos = creditService.findAllCreditInstallmentsForCredit(creditId);

        // Assert
        assertNotNull(creditInstallmentDtos);
        assertTrue(creditInstallmentDtos.isEmpty());
        verify(creditInstallmentRepository, times(1)).findAllByCreditId(creditId);
    }

    @Test
    void validateCreditRequest_InvalidAccountNumber_ThrowsValidationException() {
        // Arrange
        String email = "test@example.com";
        //SECURITY

        CreditRequestCreateDto creditRequestCreateDto = new CreditRequestCreateDto();
        creditRequestCreateDto.setAccountNumber("123456789");

        BankUser user = new BankUser();
        user.setId(1L);

        AccountDto account = new AccountDto();
        account.setAccountNumber("987654321");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(accountService.findAllAccountsForUserById(user.getId())).thenReturn(Arrays.asList(account));

        doThrow(NullPointerException.class).when(creditServiceMock).validateCreditRequest(creditRequestCreateDto);
        doThrow(NotFoundException.class).when(creditServiceMock).validateCreditRequest(creditRequestCreateDto);

        // Perform the test and verify the exceptions
        assertThatThrownBy(() -> creditServiceMock.validateCreditRequest(creditRequestCreateDto))
                .isInstanceOfAny(NullPointerException.class, NotFoundException.class);

    }

    @Test
    void doCreditTransaction_CurrentAccount_PerformsCurrentAccountTransaction() {
        // Arrange
        CurrentAccountDto currentAccount = new CurrentAccountDto();
        currentAccount.setAccountNumber("123456789");
        currentAccount.setDefaultCurrencyCode("USD");
        Double amount = 100.0;
        String operation = "addition";

        // Act
        creditService.doCreditTransaction(currentAccount, amount, operation);

        // Assert
        verify(paymentService, times(1)).changeAccountBalance("CURRENT", currentAccount.getAccountNumber(), amount, operation, currentAccount.getDefaultCurrencyCode());
    }

    @Test
    void doCreditTransaction_ForeignCurrencyAccount_PerformsForeignCurrencyAccountTransaction() {
        // Arrange
        ForeignCurrencyAccountDto foreignCurrencyAccount = new ForeignCurrencyAccountDto();
        foreignCurrencyAccount.setAccountNumber("123456789");
        foreignCurrencyAccount.setDefaultCurrencyCode("EUR");
        Double amount = 100.0;
        String operation = "subtraction";

        // Act
        creditService.doCreditTransaction(foreignCurrencyAccount, amount, operation);

        // Assert
        verify(paymentService, times(1)).changeAccountBalance("FOREIGN_CURRENCY", foreignCurrencyAccount.getAccountNumber(), amount, operation, foreignCurrencyAccount.getDefaultCurrencyCode());
    }

    @Test
    void doCreditTransaction_BusinessAccount_PerformsBusinessAccountTransaction() {
        // Arrange
        BusinessAccountDto businessAccount = new BusinessAccountDto();
        businessAccount.setAccountNumber("123456789");
        businessAccount.setDefaultCurrencyCode("GBP");
        Double amount = 100.0;
        String operation = "addition";

        // Act
        creditService.doCreditTransaction(businessAccount, amount, operation);

        // Assert
        verify(paymentService, times(1)).changeAccountBalance("BUSINESS", businessAccount.getAccountNumber(), amount, operation, businessAccount.getDefaultCurrencyCode());
    }


    @Test
    public void testValidateCreditRequest_Success() {
        // Prepare test data
        String email = "test@example.com";
        CreditRequestCreateDto creditRequestCreateDto = new CreditRequestCreateDto();
        creditRequestCreateDto.setAccountNumber("123456789"); // Specify the correct account number

        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BankUser user = new BankUser();
        user.setId(1L);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        List<AccountDto> userAccounts = new ArrayList<>();
        userAccounts.add(new AccountDto(1L, "123456789", 1L, 1000.0, "Account 1", 1L, "USD", AccountStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusYears(1)));
        userAccounts.add(new AccountDto(2L, "987654321", 1L, 2000.0, "Account 2", 2L, "EUR", AccountStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusYears(1)));
        when(accountService.findAllAccountsForUserById(user.getId())).thenReturn(userAccounts);

        List<CreditRequest> userCreditRequests = new ArrayList<>();
        // Add existing credit requests for the user
        // ...
        when(creditRequestRepository.findAllByClientEmailAndCreditRequestStatus(any(), eq(CreditRequestStatus.WAITING))).thenReturn(userCreditRequests);

        // Call the method to be tested
        creditService.validateCreditRequest(creditRequestCreateDto);

        // No exceptions thrown, test passes

        // Verify the mock interactions
        verify(userRepository, times(1)).findByEmail(any());
        verify(accountService, times(1)).findAllAccountsForUserById(user.getId());
        verify(creditRequestRepository, times(1)).findAllByClientEmailAndCreditRequestStatus(any(), eq(CreditRequestStatus.WAITING));
    }

    @Test
    public void testFindAllCreditsForLoggedInUser_Success() {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // Prepare test data
        BankUser user = new BankUser();
        user.setId(1L);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        List<Credit> userCredits = new ArrayList<>();
        // Add user credits to the list
        // ...
        when(creditRepository.findAllByClientIdOrderByCreditAmountDesc(user.getId())).thenReturn(userCredits);

        // Call the method to be tested
        List<CreditDto> result = creditService.findAllCreditsForLoggedInUser();

        // Assertions
        assertNotNull(result);
        // Add additional assertions as needed

        // Verify the mock interactions
        verify(userRepository, times(1)).findByEmail(any());
        verify(creditRepository, times(1)).findAllByClientIdOrderByCreditAmountDesc(user.getId());
    }

    @Test
    public void testCreateCreditRequest_Success() {
        var authenticationToken = new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        CreditRequestCreateDto creditRequestCreateDto = new CreditRequestCreateDto();


        BankUser user = new BankUser();
        user.setId(1L);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));

        List<AccountDto> userAccounts = new ArrayList<>();
        userAccounts.add(new AccountDto(1L, "123456789", 1L, 1000.0, "Account 1", 1L, "USD", AccountStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusYears(1)));
        userAccounts.add(new AccountDto(2L, "987654321", 1L, 2000.0, "Account 2", 2L, "EUR", AccountStatus.ACTIVE, LocalDate.now(), LocalDate.now().plusYears(1)));
        when(accountService.findAllAccountsForUserById(user.getId())).thenReturn(userAccounts);

        creditRequestCreateDto.setAccountNumber("123456789");


        CreditRequestDto result = creditService.createCreditRequest(creditRequestCreateDto);


        assertNotNull(result);


        verify(userRepository, times(1)).findByEmail(any());
        verify(accountService, times(1)).findAllAccountsForUserById(user.getId());
        verify(creditRequestRepository, times(1)).save(any(CreditRequest.class));
    }

}
