package org.banka1.bankaservice.services;

import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyBalanceDto;
import org.banka1.bankservice.domains.dtos.payment.*;
import org.banka1.bankservice.domains.entities.account.CurrentAccount;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.payment.PaymentReceiver;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.repositories.*;
import org.banka1.bankservice.services.AccountService;
import org.banka1.bankservice.services.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentReceiverRepository paymentReceiverRepository;
    private CurrentAccountRepository currentAccountRepository;
    private ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private BusinessAccountRepository businessAccountRepository;
    private ForeignCurrencyBalanceRepository foreignCurrencyBalanceRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    private PaymentService paymentService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        paymentRepository = Mockito.mock(PaymentRepository.class);
        paymentReceiverRepository = Mockito.mock(PaymentReceiverRepository.class);
        currentAccountRepository = Mockito.mock(CurrentAccountRepository.class);
        foreignCurrencyAccountRepository = Mockito.mock(ForeignCurrencyAccountRepository.class);
        businessAccountRepository = Mockito.mock(BusinessAccountRepository.class);
        foreignCurrencyBalanceRepository = Mockito.mock(ForeignCurrencyBalanceRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        accountService = Mockito.mock(AccountService.class);

        paymentService = new PaymentService(paymentRepository, paymentReceiverRepository,
                currentAccountRepository, foreignCurrencyAccountRepository,
                businessAccountRepository, foreignCurrencyBalanceRepository,
                userRepository, accountService);
    }

    @Test
    public void makePayment_SuccessfulTest() {
        PaymentCreateDto paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setSenderAccountNumber("senderAccountNumber");
        paymentCreateDto.setReceiverAccountNumber("receiverAccountNumber");
        paymentCreateDto.setAmount(100.0);

        String email = "test@example.com";
        BankUser user = new BankUser();
        user.setId(1L);
        Optional<BankUser> userOptional = Optional.of(user);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(userOptional);

        Payment payment = new Payment();
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(payment);

        assertThrows(NotFoundException.class, () -> paymentService.makePayment(paymentCreateDto));
    }

    @Test
    public void makePayment_InvalidSenderAccount_ThrowsException() {
        PaymentService paymentService = new PaymentService(paymentRepository, paymentReceiverRepository,
                currentAccountRepository, foreignCurrencyAccountRepository,
                businessAccountRepository, foreignCurrencyBalanceRepository,
                userRepository, accountService);

        PaymentCreateDto paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setSenderAccountNumber("invalidSender");
        paymentCreateDto.setReceiverAccountNumber("receiverAccount");
        paymentCreateDto.setAmount(100.0);

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new BankUser()));

        Mockito.when(currentAccountRepository.findByAccountNumber("invalidSender")).thenThrow(new RuntimeException("Invalid sender account"));

        assertThrows(RuntimeException.class, () -> paymentService.makePayment(paymentCreateDto));
    }

    @Test
    public void transferMoney_Successful() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccountNumber("senderAccount");
        moneyTransferDto.setReceiverAccountNumber("receiverAccount");
        moneyTransferDto.setAmount(100.0);
        moneyTransferDto.setCurrencySymbol("RSD");

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new BankUser()));
        Mockito.when(paymentRepository.save(Mockito.any(Payment.class))).thenReturn(new Payment());

        assertThrows(NotFoundException.class, () -> paymentService.transferMoney(moneyTransferDto));
    }

    @Test
    public void transferMoney_InvalidSenderAccount_ThrowsException() {
        MoneyTransferDto moneyTransferDto = new MoneyTransferDto();
        moneyTransferDto.setSenderAccountNumber("invalidSender");
        moneyTransferDto.setReceiverAccountNumber("receiverAccount");
        moneyTransferDto.setAmount(100.0);
        moneyTransferDto.setCurrencySymbol("RSD");

        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(new BankUser()));

        assertThrows(NotFoundException.class, () -> paymentService.transferMoney(moneyTransferDto));
    }

    @Test
    public void changeAccountBalance_Successful_CurrentAccount() {
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setAccountNumber("senderAccount");
        currentAccount.setAccountBalance(1000.0);
        Mockito.when(currentAccountRepository.findByAccountNumber("senderAccount")).thenReturn(Optional.of(currentAccount));

        paymentService.changeAccountBalance("CURRENT", "senderAccount", 100.0, "subtraction", "RSD");

        assertEquals(900.0, currentAccount.getAccountBalance());
        Mockito.verify(currentAccountRepository).save(currentAccount);
    }

    @Test
    public void validatePayment_Successful() {
        String senderAccountNumber = "senderAccount";
        String receiverAccountNumber = "receiverAccount";

        List<AccountDto> userAccounts = new ArrayList<>();

        Mockito.when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        assertThrows(NotFoundException.class, () -> paymentService.validatePayment(senderAccountNumber, receiverAccountNumber, 10.0));
    }

    @Test
    public void validateMoneyTransfer_UnsuccessfulTest() {
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbol = "RSD";

        List<AccountDto> userAccounts = new ArrayList<>();
        AccountDto senderAccount = new AccountDto();
        senderAccount.setAccountNumber("differentSenderAccountNumber");
        userAccounts.add(senderAccount);
        Mockito.when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        try {
            String[] result = paymentService.validateMoneyTransfer(senderAccountNumber, receiverAccountNumber, amount, currencySymbol);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("Sender account has not been found among user's accounts.", ex.getMessage());
        }
    }

    @Test
    public void findPaymentById_SuccessfulTest() {
        Long paymentId = 1L;

        Payment payment = new Payment();
        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        PaymentDto result = paymentService.findPaymentById(paymentId);

        assertNotNull(result);
    }

    @Test
    public void findPaymentById_UnsuccessfulTest() {
        Long paymentId = 1L;

        Mockito.when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        try {
            PaymentDto result = paymentService.findPaymentById(paymentId);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("Payment has not been found.", ex.getMessage());
        }
    }

    @Test
    public void findAllPaymentsForLoggedInUser_SuccessfulTest() {
        String email = "test@example.com";
        BankUser user = new BankUser();
        user.setId(1L);

        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Payment payment1 = new Payment();
        Payment payment2 = new Payment();
        List<Payment> payments = Arrays.asList(payment1, payment2);
        Mockito.when(paymentRepository.findAllBySenderId(user.getId())).thenReturn(payments);

        List<PaymentDto> result = paymentService.findAllPaymentsForLoggedInUser();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void findAllPaymentsForLoggedInUser_UnsuccessfulTest() {
        String email = "test@example.com";

        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());


        try {
            List<PaymentDto> result = paymentService.findAllPaymentsForLoggedInUser();

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("User has not been found.", ex.getMessage());
        }
    }

    @Test
    public void findAllPaymentsForAccount_SuccessfulTest() {
        String accountNumber = "accountNumber";

        Payment payment1 = new Payment();
        Payment payment2 = new Payment();
        List<Payment> payments = Arrays.asList(payment1, payment2);
        Mockito.when(paymentRepository.findAllBySenderAccountNumber(accountNumber)).thenReturn(payments);

        List<PaymentDto> result = paymentService.findAllPaymentsForAccount(accountNumber);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void findAllPaymentsForAccount_UnsuccessfulTest() {
        String accountNumber = "accountNumber";

        Mockito.when(paymentRepository.findAllBySenderAccountNumber(accountNumber)).thenReturn(Collections.emptyList());

        List<PaymentDto> result = paymentService.findAllPaymentsForAccount(accountNumber);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void findPaymentReceiverById_SuccessfulTest() {
        Long paymentReceiverId = 1L;

        PaymentReceiver paymentReceiver = new PaymentReceiver();
        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.of(paymentReceiver));

        PaymentReceiverDto result = paymentService.findPaymentReceiverById(paymentReceiverId);

        assertNotNull(result);
    }

    @Test
    public void findPaymentReceiverById_UnsuccessfulTest() {
        Long paymentReceiverId = 1L;

        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.empty());

        try {
            PaymentReceiverDto result = paymentService.findPaymentReceiverById(paymentReceiverId);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("Payment receiver has not been found.", ex.getMessage());
        }
    }

    @Test
    public void findAllPaymentReceiversForLoggedInUser_SuccessfulTest() {
        String email = "test@example.com";
        BankUser user = new BankUser();
        user.setId(1L);

        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        PaymentReceiver paymentReceiver1 = new PaymentReceiver();
        PaymentReceiver paymentReceiver2 = new PaymentReceiver();
        List<PaymentReceiver> paymentReceivers = Arrays.asList(paymentReceiver1, paymentReceiver2);
        Mockito.when(paymentReceiverRepository.findAllBySenderId(user.getId())).thenReturn(paymentReceivers);

        List<PaymentReceiverDto> result = paymentService.findAllPaymentReceiversForLoggedInUser();

        assertNotNull(result);
        assertEquals(2, result.size());
    }


    @Test
    public void findAllPaymentReceiversForLoggedInUser_UnsuccessfulTest() {
        String email = "test@example.com";

        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        try {
            List<PaymentReceiverDto> result = paymentService.findAllPaymentReceiversForLoggedInUser();

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("User has not been found.", ex.getMessage());
        }
    }

    @Test
    public void createPaymentReceiver_SuccessfulTest() {
        PaymentReceiverCreateDto paymentReceiverCreateDto = new PaymentReceiverCreateDto();

        String email = "test@example.com";
        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        BankUser user = new BankUser();
        user.setId(1L);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        PaymentReceiverDto result = paymentService.createPaymentReceiver(paymentReceiverCreateDto);

        assertNotNull(result);
    }

    @Test
    public void createPaymentReceiver_UnsuccessfulTest() {
        PaymentReceiverCreateDto paymentReceiverCreateDto = new PaymentReceiverCreateDto();

        String email = "test@example.com";
        SecurityContext securityContextMock = Mockito.mock(SecurityContext.class);
        Authentication authenticationMock = Mockito.mock(Authentication.class);
        Mockito.when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
        Mockito.when(authenticationMock.getName()).thenReturn(email);
        SecurityContextHolder.setContext(securityContextMock);

        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        try {
            PaymentReceiverDto result = paymentService.createPaymentReceiver(paymentReceiverCreateDto);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("User has not been found.", ex.getMessage());
        }
    }

    @Test
    public void updatePaymentReceiver_SuccessfulTest() {
        Long paymentReceiverId = 1L;
        PaymentReceiverUpdateDto paymentReceiverUpdateDto = new PaymentReceiverUpdateDto();

        PaymentReceiver paymentReceiver = new PaymentReceiver();
        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.of(paymentReceiver));

        PaymentReceiverDto result = paymentService.updatePaymentReceiver(paymentReceiverUpdateDto, paymentReceiverId);

        assertNotNull(result);
    }

    @Test
    public void updatePaymentReceiver_UnsuccessfulTest() {
        Long paymentReceiverId = 1L;
        PaymentReceiverUpdateDto paymentReceiverUpdateDto = new PaymentReceiverUpdateDto();

        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.empty());

        try {
            PaymentReceiverDto result = paymentService.updatePaymentReceiver(paymentReceiverUpdateDto, paymentReceiverId);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("Payment receiver has not been found.", ex.getMessage());
        }
    }

    @Test
    public void deletePaymentReceiver_SuccessfulTest() {
        Long paymentReceiverId = 1L;

        PaymentReceiver paymentReceiver = new PaymentReceiver();
        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.of(paymentReceiver));

        String result = paymentService.deletePaymentReceiver(paymentReceiverId);

        assertNotNull(result);
        assertEquals("Payment receiver has been successfully deleted.", result);
    }

    @Test
    public void deletePaymentReceiver_UnsuccessfulTest() {
        Long paymentReceiverId = 1L;

        Mockito.when(paymentReceiverRepository.findById(paymentReceiverId)).thenReturn(Optional.empty());

        try {
            String result = paymentService.deletePaymentReceiver(paymentReceiverId);

            fail("NotFoundException should have been thrown.");
        } catch (NotFoundException ex) {
            assertEquals("Payment receiver has not been found.", ex.getMessage());
        }
    }

    @Test
    public void validateFurtherAndReturnTypes_SuccessfulTest() {
        AccountDto senderAccount = new CurrentAccountDto();
        senderAccount.setAccountBalance(1000.0); // Set a non-null value for accountBalance
        AccountDto receiverAccount = new CurrentAccountDto();
        Double amount = 100.0;
        boolean conversion = false;
        String currencySymbolOne = "RSD";
        String currencySymbolTwo = "USD";

        String[] result = paymentService.validateFurtherAndReturnTypes(senderAccount, receiverAccount, amount, conversion, currencySymbolOne, currencySymbolTwo);

        assertNotNull(result);
    }


    @Test
    public void validateFurtherAndReturnTypes_UnsuccessfulTest() {
        AccountDto senderAccount = new CurrentAccountDto();
        senderAccount.setAccountBalance(50.0);
        AccountDto receiverAccount = new CurrentAccountDto();
        Double amount = 100.0;
        boolean conversion = false;
        String currencySymbolOne = "RSD";
        String currencySymbolTwo = "USD";

        try {
            String[] result = paymentService.validateFurtherAndReturnTypes(senderAccount, receiverAccount, amount, conversion, currencySymbolOne, currencySymbolTwo);

            fail("ValidationException should have been thrown.");
        } catch (ValidationException ex) {
            assertEquals("The amount of funds on the sender account is not high enough to successfully complete the transaction.", ex.getMessage());
        }
    }

    @Test
    public void validateCurrencyOnForeignCurrencyAccount_SuccessfulTest() {
        ForeignCurrencyAccountDto account = new ForeignCurrencyAccountDto();
        account.setForeignCurrencyBalances(Arrays.asList(
                new ForeignCurrencyBalanceDto(1L, "USD", 1000.0),
                new ForeignCurrencyBalanceDto(2L, "EUR", 500.0)
        ));
        String currencySymbol = "USD";

        Double result = paymentService.validateCurrencyOnForeignCurrencyAccount(account, currencySymbol);

        assertNotNull(result);
    }

    @Test
    public void validateCurrencyOnForeignCurrencyAccount_UnsuccessfulTest() {
        ForeignCurrencyAccountDto account = new ForeignCurrencyAccountDto();
        account.setForeignCurrencyBalances(Arrays.asList(
                new ForeignCurrencyBalanceDto(1L, "USD", 1000.0),
                new ForeignCurrencyBalanceDto(2L, "EUR", 500.0)
        ));
        String currencySymbol = "GBP";

        try {
            Double result = paymentService.validateCurrencyOnForeignCurrencyAccount(account, currencySymbol);

            fail("ValidationException should have been thrown.");
        } catch (ValidationException ex) {
            assertEquals("Currency GBP is not present on foreign currency account.", ex.getMessage());
        }
    }


}
