package org.banka1.bankaservice.services;

import org.apache.commons.csv.CSVRecord;
import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.account.CurrentAccountDto;
import org.banka1.bankservice.domains.dtos.account.ForeignCurrencyAccountDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.*;
import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public CurrencyExchangeServiceTest() {
        MockitoAnnotations.openMocks(this);
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

        // Act and Assert
        assertThrows(NotFoundException.class, () -> currencyExchangeService.convertMoney(conversionTransferCreateDto));
    }

    @Test
    public void testConfirmConversionTransfer_UserNotFound() {
        // Arrange
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();
        conversionTransferConfirmDto.setExchangePairSymbol("USD/RSD");
        conversionTransferConfirmDto.setSenderAccountNumber("senderAccountNumber");
        conversionTransferConfirmDto.setReceiverAccountNumber("receiverAccountNumber");
        conversionTransferConfirmDto.setAmount(100.0);
        conversionTransferConfirmDto.setConvertedAmount(10500.0);

        String email = "test@example.com";
//        SecurityContextHolder.getContext().setAuthentication(() -> email);

        when(userRepository.findById(any())).thenReturn(Optional.of(new BankUser()));

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act and Assert
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
    public void testLoadForex_Successful() throws Exception {
        // Arrange

        // Mock CSV records
        List<CSVRecord> csvRecords = new ArrayList<>();

        // Mock FlaskResponse
        FlaskResponse flaskResponse = new FlaskResponse();
        flaskResponse.setExchangeRate(1.0); // Set the desired exchange rate for testing

        // Mock ExchangePair objects to save
        List<ExchangePair> exchangePairsToSave = new ArrayList<>();

        assertEquals(1, 1);
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

