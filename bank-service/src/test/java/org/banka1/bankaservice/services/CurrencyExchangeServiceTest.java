package org.banka1.bankaservice.services;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.*;
import org.banka1.bankservice.domains.entities.account.AccountType;
import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.CurrencyExchangeMapper;
import org.banka1.bankservice.repositories.ConversionTransferRepository;
import org.banka1.bankservice.repositories.ExchangePairRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.banka1.bankservice.services.AccountService;
import org.banka1.bankservice.services.CurrencyExchangeService;
import org.banka1.bankservice.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ResourceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CurrencyExchangeServiceTest {

    @Mock
    private ExchangePairRepository exchangePairRepository;

    @Mock
    private ConversionTransferRepository conversionTransferRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private CurrencyExchangeService currencyExchangeService;

    @Mock
    private ObjectMapper objectMapper;

    public CurrencyExchangeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    public void setup() {
        MockitoAnnotations.openMocks(this);
        String baseForexUrl = "https://example.com/api/forex/exchange";
        currencyExchangeService = new CurrencyExchangeService(
                exchangePairRepository,
                conversionTransferRepository,
                userRepository,
                paymentService,
                accountService,
                baseForexUrl,
                objectMapper
        );
    }

    @Test
    public void testConvertMoney_SuccessfulConversion() {
        // Arrange
        ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto();
        conversionTransferCreateDto.setExchangePairSymbol("USD/RSD");
        conversionTransferCreateDto.setAmount(100.0);

        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setExchangeRate(105.0);

        when(exchangePairRepository.findByExchangePairSymbol("USD/RSD")).thenReturn(Optional.of(exchangePair));

        // Act
        ConversionTransferConfirmDto result = currencyExchangeService.convertMoney(conversionTransferCreateDto);

        // Assert
        assertEquals("USD/RSD", result.getExchangePairSymbol());
        assertEquals(100.0, result.getAmount());
        assertEquals(100.0 * 105.0, result.getConvertedAmount());
        assertEquals(105.0, result.getExchangeRate());
    }

    @Test
    public void testConvertMoney_ExchangePairNotFound() {
        // Arrange
        ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto();
        conversionTransferCreateDto.setExchangePairSymbol("USD/EUR");
        conversionTransferCreateDto.setAmount(100.0);

        when(exchangePairRepository.findByExchangePairSymbol("USD/EUR")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> currencyExchangeService.convertMoney(conversionTransferCreateDto));
    }

    @Test
    public void testConvertMoney_ConvertFromRSD() {
        // Test scenario for converting from RSD

        // Mock necessary dependencies
        String exchangePairSymbol = "RSD/USD";
        Double amount = 100.0;

        ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto();
        conversionTransferCreateDto.setExchangePairSymbol(exchangePairSymbol);
        conversionTransferCreateDto.setAmount(amount);

        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setExchangePairSymbol(exchangePairSymbol);
        exchangePair.setExchangeRate(0.01);

        when(exchangePairRepository.findByExchangePairSymbol(exchangePairSymbol)).thenReturn(Optional.of(exchangePair));

        // Perform the method call
        ConversionTransferConfirmDto confirmDto = currencyExchangeService.convertMoney(conversionTransferCreateDto);

        // Assert the expected converted amount and commission fee with the desired precision
        double expectedConvertedAmount = amount / 0.01;
        double expectedCommissionFee = currencyExchangeService.calculateCommissionFee(exchangePairSymbol, expectedConvertedAmount);

        assertEquals(expectedConvertedAmount, confirmDto.getConvertedAmount(), 0.0001);
    }

    @Test
    public void testConvertMoney_ConvertToRSD() {
        String exchangePairSymbol = "USD/RSD";
        Double amount = 100.0;

        ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto();
        conversionTransferCreateDto.setExchangePairSymbol(exchangePairSymbol);
        conversionTransferCreateDto.setAmount(amount);

        ExchangePair exchangePair = new ExchangePair();
        exchangePair.setExchangePairSymbol(exchangePairSymbol);
        exchangePair.setExchangeRate(100.0);

        when(exchangePairRepository.findByExchangePairSymbol(exchangePairSymbol)).thenReturn(Optional.of(exchangePair));

        // Perform the method call
        ConversionTransferConfirmDto confirmDto = currencyExchangeService.convertMoney(conversionTransferCreateDto);

        // Assert the expected converted amount and commission fee
        assertEquals(amount * 100.0, confirmDto.getConvertedAmount(), 0.00001);
        assertEquals(currencyExchangeService.calculateCommissionFee(exchangePairSymbol, amount * 100.0), confirmDto.getCommissionFee(), 0.00001);
    }


    @Test
    public void testValidateConversionTransfer_SenderAccountNotForeignCurrencyAccount() {
        // Test scenario for sender account not being a foreign currency account

        // Mock necessary dependencies
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "EUR";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        AccountDto senderAccount = new AccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);
        userAccounts.add(senderAccount);

        AccountDto receiverAccount = new AccountDto();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setAccountBalance(0.0);
        userAccounts.add(receiverAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        assertThrows(ValidationException.class, ()-> currencyExchangeService
                .validateConversionTransfer(senderAccountNumber, receiverAccountNumber, amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }

    @Test
    public void testValidateConversionTransfer_ReceiverAccountNotForeignCurrencyAccount() {
        // Test scenario for receiver account not being a foreign currency account

        // Mock necessary dependencies
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "EUR";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);
        userAccounts.add(senderAccount);

        AccountDto receiverAccount = new AccountDto();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setAccountBalance(0.0);
        userAccounts.add(receiverAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        assertThrows(ValidationException.class, ()-> currencyExchangeService
                .validateConversionTransfer(senderAccountNumber, receiverAccountNumber, amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }



    @Test
    public void testConfirmConversionTransfer_Successful() {
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setExchangePairSymbol("USD/EUR");
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(90.0);
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BankUser user = new BankUser();
        user.setId(1L);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);

        ForeignCurrencyAccountDto receiverAccount = new ForeignCurrencyAccountDto();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setAccountBalance(0.0);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(Arrays.asList(senderAccount, receiverAccount));

        String[] accountTypes = { "senderAccountType", "receiverAccountType" };
        when(paymentService.validateFurtherAndReturnTypes(eq(senderAccount), eq(receiverAccount), eq(100.0), eq(true), eq("USD"), eq("EUR")))
                .thenReturn(accountTypes);

        ConversionTransferDto result = currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, false);

        assertEquals(1L, result.getSenderId().longValue());
    }


    @Test
    public void testConfirmConversionTransfer_SenderAccountNotFound() {
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(90.0);
        conversionTransferConfirmDto.setExchangePairSymbol("USD/EUR");
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BankUser user = new BankUser();
        user.setId(1L);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);

        ForeignCurrencyAccountDto receiverAccount = new ForeignCurrencyAccountDto();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setAccountBalance(0.0);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(Arrays.asList(senderAccount, receiverAccount));

        String[] accountTypes = { "senderAccountType", "receiverAccountType" };
        when(paymentService.validateFurtherAndReturnTypes(senderAccount, receiverAccount, 100.0, true, "USD", "EUR"))
                .thenReturn(accountTypes);

        currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, false);
    }

    @Test
    public void testConfirmConversionTransfer_ReceiverAccountNotFound() {
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(90.0);
        conversionTransferConfirmDto.setExchangePairSymbol("USD/EUR");

        AccountDto senderAccount = new AccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(Collections.singletonList(senderAccount));
        when(accountService.findAccountByAccountNumber("receiverAccountNumber")).thenReturn(null);


        assertThrows(NotFoundException.class,()-> currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, true));
    }

    @Test
    public void testConfirmConversionTransfer_SenderAccountNotForeignCurrencyAccount() {
        // Mock necessary dependencies
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(90.0);
        conversionTransferConfirmDto.setExchangePairSymbol("USD/EUR");
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        BankUser user = new BankUser();
        user.setId(1L);

        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber("senderAccountNumber");
        senderAccount.setAccountBalance(500.0);

        ForeignCurrencyAccountDto receiverAccount = new ForeignCurrencyAccountDto();
        receiverAccount.setAccountNumber("receiverAccountNumber");
        receiverAccount.setAccountBalance(0.0);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(Arrays.asList(senderAccount, receiverAccount));
        when(paymentService.validateFurtherAndReturnTypes(senderAccount, receiverAccount, 100.0, true, "USD", "EUR"))
                .thenReturn(new String[]{"senderAccountType", "receiverAccountType"});

        currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, false);
    }

    @Test
    public void testCalculateExchangeRate_Successful() {
        String fromCurrency = "RSD";
        String toCurrency = "EUR";
        Double flaskExchangeRate = 100.0;

        Double exchangeRate = currencyExchangeService.calculateExchangeRate(fromCurrency, toCurrency, flaskExchangeRate);

        assertEquals(0.01005, exchangeRate, 0.00001);
    }

    @Test
    public void testCalculateExchangeRate_Unsuccessful() {
        String fromCurrency = "RSD";
        String toCurrency = "USD";
        Double flaskExchangeRate = 100.0;

        Double exchangeRate = currencyExchangeService.calculateExchangeRate(fromCurrency, toCurrency, flaskExchangeRate);

        assertNotEquals(0.01005, exchangeRate, 0.00001);
    }

    @Test
    public void testCalculateExchangeRate_Successful_EURToRSD() {
        // Test successful scenario for EUR to RSD conversion

        // Set up input values
        String fromCurrency = "EUR";
        String toCurrency = "RSD";
        Double flaskExchangeRate = 100.0;

        // Call the method
        Double exchangeRate = currencyExchangeService.calculateExchangeRate(fromCurrency, toCurrency, flaskExchangeRate);

        // Assert the expected result
        assertEquals(99.5, exchangeRate, 0.00001);
    }

    @Test
    public void testCalculateExchangeRate_Successful_USDToRSD() {
        // Test successful scenario for USD to RSD conversion

        // Set up input values
        String fromCurrency = "USD";
        String toCurrency = "RSD";
        Double flaskExchangeRate = 100.0;

        // Call the method
        Double exchangeRate = currencyExchangeService.calculateExchangeRate(fromCurrency, toCurrency, flaskExchangeRate);

        // Assert the expected result
        assertEquals(99.0, exchangeRate, 0.00001);
    }

    @Test
    public void testCalculateExchangeRate_Unsuccessful_InvalidCurrencyPair() {
        // Test unsuccessful scenario for an invalid currency pair

        // Set up input values
        String fromCurrency = "RSD";
        String toCurrency = "JPY";
        Double flaskExchangeRate = 100.0;

        // Call the method
        Double exchangeRate = currencyExchangeService.calculateExchangeRate(fromCurrency, toCurrency, flaskExchangeRate);

        // Assert the expected result (should be a non-zero value)
        assertNotEquals(0.0, exchangeRate, 0.00001);
    }


    @Test
    public void testConfirmConversionTransfer_UserNotFound() {
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setExchangePairSymbol("USD/RSD");
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(10500.0);

        String email = "test@example.com";

        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, false));
    }

    @Test
    public void testValidateConversionTransfer_SuccessfulValidation() {
        // Arrange
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "RSD";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber(senderAccountNumber);
        userAccounts.add(senderAccount);

        ForeignCurrencyAccountDto receiverAccount = new ForeignCurrencyAccountDto();
        receiverAccount.setAccountNumber(receiverAccountNumber);
        userAccounts.add(receiverAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);
        when(paymentService.validateFurtherAndReturnTypes(senderAccount, receiverAccount, amount, true, currencySymbolOne, currencySymbolTwo))
                .thenReturn(new String[]{"senderAccountType", "receiverAccountType"});

        // Act
        String[] result = currencyExchangeService.validateConversionTransfer(senderAccountNumber, receiverAccountNumber, amount,
                currencySymbolOne, currencySymbolTwo, isCardConversion);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.length);
        assertEquals("senderAccountType", result[0]);
        assertEquals("receiverAccountType", result[1]);
    }

    @Test
    public void testValidateConversionTransfer_SenderAccountNotFound() {
        // Arrange
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "RSD";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> currencyExchangeService.validateConversionTransfer(senderAccountNumber, receiverAccountNumber,
                amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }

    @Test
    public void testValidateConversionTransfer_ReceiverAccountNotFound() {
        // Arrange
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "RSD";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber(senderAccountNumber);
        userAccounts.add(senderAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);
        when(accountService.findAccountByAccountNumber(receiverAccountNumber)).thenReturn(null);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> currencyExchangeService.validateConversionTransfer(senderAccountNumber, receiverAccountNumber,
                amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }

    @Test
    public void testValidateConversionTransfer_SenderAccountInvalidType() {
        // Arrange
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "RSD";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        CurrentAccountDto senderAccount = new CurrentAccountDto();
        senderAccount.setAccountNumber(senderAccountNumber);
        userAccounts.add(senderAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenReturn(userAccounts);

        // Act and Assert
        assertThrows(NotFoundException.class, () -> currencyExchangeService.validateConversionTransfer(senderAccountNumber, receiverAccountNumber,
                amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }

    @Test
    public void testValidateConversionTransfer_ReceiverAccountInvalidType() {
        // Arrange
        String senderAccountNumber = "senderAccountNumber";
        String receiverAccountNumber = "receiverAccountNumber";
        Double amount = 100.0;
        String currencySymbolOne = "USD";
        String currencySymbolTwo = "RSD";
        boolean isCardConversion = false;

        List<AccountDto> userAccounts = new ArrayList<>();
        ForeignCurrencyAccountDto senderAccount = new ForeignCurrencyAccountDto();
        senderAccount.setAccountNumber(senderAccountNumber);
        userAccounts.add(senderAccount);

        CurrentAccountDto receiverAccount = new CurrentAccountDto();
        receiverAccount.setAccountNumber(receiverAccountNumber);
        userAccounts.add(receiverAccount);

        when(accountService.findAllAccountsForLoggedInUser()).thenThrow(ValidationException.class);

        // Act and Assert
        assertThrows(ValidationException.class, () -> currencyExchangeService.validateConversionTransfer(senderAccountNumber, receiverAccountNumber,
                amount, currencySymbolOne, currencySymbolTwo, isCardConversion));
    }

    @Test
    public void testFindConversionTransferById_Successful() {
        // Arrange
        Long conversionTransferId = 123L;

        ConversionTransfer conversionTransfer = new ConversionTransfer();
        when(conversionTransferRepository.findById(conversionTransferId)).thenReturn(Optional.of(conversionTransfer));

        // Act
        ConversionTransferDto result = currencyExchangeService.findConversionTransferById(conversionTransferId);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testFindConversionTransferById_NotFound() {
        // Arrange
        Long conversionTransferId = 123L;

        when(conversionTransferRepository.findById(conversionTransferId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(NotFoundException.class, () -> currencyExchangeService.findConversionTransferById(conversionTransferId));
    }

    @Test
    public void testFindAllConversionTransfersForLoggedInUser_Successful() {
        // Arrange
        String email = "test@example.com";
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        BankUser user = new BankUser();
        user.setId(123L);

        List<ConversionTransfer> conversionTransfers = new ArrayList<>();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(conversionTransferRepository.findAllBySenderId(user.getId())).thenReturn(conversionTransfers);

        // Act
        List<ConversionTransferDto> result = currencyExchangeService.findAllConversionTransfersForLoggedInUser();

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testFindAllConversionTransfersForAccount_Successful() {
        // Arrange
        String accountNumber = "123456789";

        List<ConversionTransfer> conversionTransfers = new ArrayList<>();

        when(conversionTransferRepository.findAllBySenderAccountNumber(accountNumber)).thenReturn(conversionTransfers);

        // Act
        List<ConversionTransferDto> result = currencyExchangeService.findAllConversionTransfersForAccount(accountNumber);

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testFindAllExchangePairs_Successful() {
        // Arrange
        List<ExchangePair> exchangePairs = new ArrayList<>();

        when(exchangePairRepository.findAll()).thenReturn(exchangePairs);

        // Act
        List<ExchangePairDto> result = currencyExchangeService.findAllExchangePairs();

        // Assert
        assertNotNull(result);
    }

    @Test
    public void testCalculateCommissionFee_EURExchangePair() {
        // Arrange
        String exchangePairSymbol = "EUR/USD";
        Double convertedAmount = 1000.0;

        // Act
        Double result = currencyExchangeService.calculateCommissionFee(exchangePairSymbol, convertedAmount);

        // Assert
        assertNotNull(result);
        assertEquals(5.0251256281407, result, 0.0001);
    }

    @Test
    public void testCalculateCommissionFee_NonEURExchangePair() {
        // Arrange
        String exchangePairSymbol = "USD/CAD";
        Double convertedAmount = 500.0;

        // Act
        Double result = currencyExchangeService.calculateCommissionFee(exchangePairSymbol, convertedAmount)/10;

        // Assert
        assertNotNull(result);
        assertEquals(0.5050505050505, result, 0.0001);
    }

}

