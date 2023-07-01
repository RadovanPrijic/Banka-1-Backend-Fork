package org.banka1.bankservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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
import org.banka1.bankservice.domains.mappers.CurrencyExchangeMapper;
import org.banka1.bankservice.repositories.ConversionTransferRepository;
import org.banka1.bankservice.repositories.ExchangePairRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CurrencyExchangeService {

    private final ExchangePairRepository exchangePairRepository;
    private final ConversionTransferRepository conversionTransferRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final AccountService accountService;
    private final ObjectMapper objectMapper;

//    @Value("${flask.api.forex.exchange}")
    private String baseForexUrl;

    public CurrencyExchangeService(ExchangePairRepository exchangePairRepository, ConversionTransferRepository conversionTransferRepository,
                                   UserRepository userRepository, PaymentService paymentService, AccountService accountService,
                                   @Value("${flask.api.forex.exchange}") String baseForexUrl, @Autowired ObjectMapper objectMapper) {
        this.exchangePairRepository = exchangePairRepository;
        this.conversionTransferRepository = conversionTransferRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.accountService = accountService;
        this.baseForexUrl = baseForexUrl;
        this.objectMapper = objectMapper;
    }

    public ConversionTransferConfirmDto convertMoney(ConversionTransferCreateDto conversionTransferCreateDto) {
        ExchangePair exchangePair = exchangePairRepository.findByExchangePairSymbol(conversionTransferCreateDto.getExchangePairSymbol()).orElseThrow(() -> new NotFoundException("Exchange pair has not been found."));
        Double exchangeRate = exchangePair.getExchangeRate();
        Double convertedAmount = conversionTransferCreateDto.getAmount() * exchangeRate;
        Double commissionFee = ((100 * convertedAmount.doubleValue()) / 99) - convertedAmount.doubleValue();

        return new ConversionTransferConfirmDto(
                conversionTransferCreateDto.getSenderAccountNumber(),
                conversionTransferCreateDto.getReceiverAccountNumber(),
                conversionTransferCreateDto.getExchangePairSymbol(),
                conversionTransferCreateDto.getAmount(),
                convertedAmount,
                exchangeRate,
                commissionFee
        );
    }

    public ConversionTransferDto confirmConversionTransfer(ConversionTransferConfirmDto conversionTransferConfirmDto) {
        String fromCurrency = conversionTransferConfirmDto.getExchangePairSymbol().split("/")[0];
        String toCurrency = conversionTransferConfirmDto.getExchangePairSymbol().split("/")[1];

        String[] accountTypes = validateConversionTransfer(conversionTransferConfirmDto.getSenderAccountNumber(),
                                                           conversionTransferConfirmDto.getReceiverAccountNumber(),
                                                           conversionTransferConfirmDto.getAmount(),
                                                           fromCurrency,
                                                           toCurrency);

        paymentService.changeAccountBalance(accountTypes[0],
                                            conversionTransferConfirmDto.getSenderAccountNumber(),
                                            conversionTransferConfirmDto.getAmount(),
                                            "subtraction",
                                            fromCurrency);

        paymentService.changeAccountBalance(accountTypes[1],
                                            conversionTransferConfirmDto.getReceiverAccountNumber(),
                                            conversionTransferConfirmDto.getConvertedAmount(),
                                            "addition",
                                            toCurrency);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<BankUser> user = userRepository.findByEmail(email);
        Long userId = user.get().getId();

        ConversionTransfer conversionTransfer = CurrencyExchangeMapper.INSTANCE.conversionTransferConfirmDtoToConversionTransfer(conversionTransferConfirmDto);
        conversionTransfer.setSenderId(userId);
        conversionTransfer.setConversionTime(LocalDateTime.now());
        conversionTransferRepository.save(conversionTransfer);

        return CurrencyExchangeMapper.INSTANCE.conversionTransferToConversionTransferDto(conversionTransfer);
    }

    public String[] validateConversionTransfer(String senderAccountNumber, String receiverAccountNumber, Double amount,
                                               String currencySymbolOne, String currencySymbolTwo) {

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

        if(currencySymbolOne.equals("RSD") && !(senderAccount instanceof CurrentAccountDto))
            throw new ValidationException("Sender account is not a current account.");

        if(!(currencySymbolOne.equals("RSD")) && !(senderAccount instanceof ForeignCurrencyAccountDto))
            throw new ValidationException("Sender account is not a foreign currency account.");

        if(currencySymbolTwo.equals("RSD") && !(receiverAccount instanceof CurrentAccountDto))
            throw new ValidationException("Receiver account is not a current account.");

        if(!(currencySymbolTwo.equals("RSD")) && !(receiverAccount instanceof ForeignCurrencyAccountDto))
            throw new ValidationException("Receiver account is not a foreign currency account.");

//        return paymentService.validateBalanceAndCurrencyAndDeclareTypes(senderAccount, receiverAccount, amount, currencySymbolOne, currencySymbolTwo);
        return null;
    }

    public ConversionTransferDto findConversionTransferById(Long id) {
        Optional<ConversionTransfer> conversionTransfer = conversionTransferRepository.findById(id);

        return conversionTransfer.map(CurrencyExchangeMapper.INSTANCE::conversionTransferToConversionTransferDto).orElseThrow(() -> new NotFoundException("Conversion transfer has not been found."));
    }

    public List<ConversionTransferDto> findAllConversionTransfersForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        Long userId = user.getId();

        return conversionTransferRepository.findAllBySenderId(userId).stream().map(CurrencyExchangeMapper.INSTANCE::conversionTransferToConversionTransferDto).collect(Collectors.toList());
    }

    public List<ConversionTransferDto> findAllConversionTransfersForAccount(String accountNumber) {

        return conversionTransferRepository.findAllBySenderAccountNumber(accountNumber).stream().map(CurrencyExchangeMapper.INSTANCE::conversionTransferToConversionTransferDto).collect(Collectors.toList());
    }

    public List<ExchangePairDto> findAllExchangePairs() {

        return exchangePairRepository.findAll().stream().map(CurrencyExchangeMapper.INSTANCE::exchangePairToExchangePairDto).collect(Collectors.toList());
    }

    public void loadForex() throws Exception {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("bank-service/csv-files/bank_forex_pairs.csv"));
        } catch (Exception e) {
            fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/bank_forex_pairs.csv"));
        }

        BufferedReader reader = new BufferedReader(fileReader);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();
        List<ExchangePair> exchangePairsToSave = new ArrayList<>();

        for(CSVRecord record: csvRecords) {
            String from = record.get("from");
            String to = record.get("to");

            String url = baseForexUrl + "?from_currency=" + from + "&to_currency=" + to;
            FlaskResponse flaskResponse = getForexFromFlask(url, FlaskResponse.class);

            ExchangePair exchangePair = new ExchangePair();
            exchangePair.setExchangePairSymbol(from + "/" + to);

            //TODO Ovde uraditi foru da se napravi prodajni i kupovni kurs banke za valute
            exchangePair.setExchangeRate(flaskResponse.getExchangeRate());

            exchangePairsToSave.add(exchangePair);
        }

        exchangePairRepository.saveAll(exchangePairsToSave);
    }

    private <T> T getForexFromFlask(String url, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            return null;

        String jsonForex = response.body();
        return objectMapper.readValue(jsonForex, clazz);
    }
}
