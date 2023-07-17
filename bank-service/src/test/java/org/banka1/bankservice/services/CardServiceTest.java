package org.banka1.bankservice.services;

import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.card.CardCreateDto;
import org.banka1.bankservice.domains.dtos.card.CardDto;
import org.banka1.bankservice.domains.dtos.card.CardPaymentDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferConfirmDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferCreateDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentCreateDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentDto;
import org.banka1.bankservice.domains.entities.account.Account;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.card.Card;
import org.banka1.bankservice.domains.entities.card.CardType;
import org.banka1.bankservice.domains.entities.payment.Payment;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.entities.user.Gender;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.repositories.CardRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CardServiceTest {
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    private PaymentService paymentService;
    private CurrencyExchangeService currencyExchangeService;

    private PasswordEncoder passwordEncoder;

    private CardService cardService;
    @BeforeEach
    void setUp() {
        this.cardRepository = mock(CardRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.accountService = mock(AccountService.class);
        this.paymentService = mock(PaymentService.class);
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.currencyExchangeService = mock(CurrencyExchangeService.class);

        this.cardService = new CardService(cardRepository, userRepository, accountService,paymentService,currencyExchangeService);
    }
    @AfterAll
    public static void clearCache(){
        Mockito.clearAllCaches();
    }

    @Test
    void createCardSuccessfully(){
        //given
        BankUser client1 = BankUser.builder()
                .id(1L)
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@useremail.com")
                .phoneNumber("0651678989")
                .homeAddress("Njegoseva 25")
                .password(passwordEncoder.encode("markomarkovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();

        var cardDto = CardCreateDto.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .cardLimit(100000D).build();

        var card = Card.builder()
                .id(1L).ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("5987632154786254")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,5))
                .expiryDate(LocalDate.of(2025,5,5))
                .cardLimit(100000D)
                .remainingUntilLimit(100000D)
                .lastLimitDate(LocalDate.of(2020,5,5))
                .cardStatus(AccountStatus.ACTIVE).build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(client1));
        when(accountService.findAllAccountsForUserById(any(Long.class))).thenReturn(getAllAccountsForUserById());
        when(cardRepository.save(any(Card.class))).thenReturn(any(Card.class));

        //when
        var result = cardService.createCard(cardDto);

        //then
        assertEquals(1, result.getOwnerId());
        assertEquals("36488030", result.getAccountNumber());
        assertNotNull(result.getCardNumber());
        assertEquals(16,result.getCardNumber().length());
        assertEquals(LocalDate.now(), result.getCreationDate());
        assertEquals(result.getCreationDate().plusYears(5), result.getExpiryDate());
        assertEquals(AccountStatus.ACTIVE,result.getCardStatus());

        verify(userRepository, times(1)).findById((any(Long.class)));
        verify(accountService, times(1)).findAllAccountsForUserById((any(Long.class)));
        verify(cardRepository, times(1)).save((any(Card.class)));
        verify(cardRepository, times(1)).findAllByAccountNumber((any(String.class)));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(accountService);
        verifyNoMoreInteractions(cardRepository);

    }

    @Test
    void createCardValidationException(){
        //given
        BankUser client1 = BankUser.builder()
                .id(1L)
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@useremail.com")
                .phoneNumber("0651678989")
                .homeAddress("Njegoseva 25")
                .password(passwordEncoder.encode("markomarkovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();
        var cardDto = CardCreateDto.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .cardLimit(100000D).build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(client1));
        when(accountService.findAllAccountsForUserById(any(Long.class))).thenReturn(getAllAccountsForUserByIdValidationException());

        //when
        //then
        assertThrows(ValidationException.class, () -> cardService.createCard(cardDto));

        verify(userRepository, times(1)).findById((any(Long.class)));
        verify(accountService, times(1)).findAllAccountsForUserById((any(Long.class)));
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(accountService);
        verifyNoMoreInteractions(cardRepository);

    }
    @Test
    void createCardValidationExceptionMoreThanThreeCards(){
        //given
        BankUser client1 = BankUser.builder()
                .id(1L)
                .firstName("Marko")
                .lastName("Markovic")
                .birthDate(LocalDate.of(1990, 10, 5))
                .gender(Gender.MALE)
                .email("marko.markovic@useremail.com")
                .phoneNumber("0651678989")
                .homeAddress("Njegoseva 25")
                .password(passwordEncoder.encode("markomarkovic"))
                .roles(List.of("ROLE_CLIENT"))
                .build();
        var cardDto = CardCreateDto.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .cardLimit(100000D).build();

        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(client1));
        when(accountService.findAllAccountsForUserById(any(Long.class))).thenReturn(getAllAccountsForUserById());
        when(cardRepository.findAllByAccountNumber(anyString())).thenReturn(getCardsForAccount());
        //when
        //then
        assertThrows(ValidationException.class, () -> cardService.createCard(cardDto));

        verify(userRepository, times(1)).findById((any(Long.class)));
        verify(accountService, times(1)).findAllAccountsForUserById((any(Long.class)));

//        verifyNoMoreInteractions(userRepository);
//        verifyNoMoreInteractions(accountService);
//        verifyNoMoreInteractions(cardRepository);

    }


    List<Card> findAllCardsForAccount(){

        var card1 = Card.builder()
                .id(2L).ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("5987632154786254")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,5))
                .expiryDate(LocalDate.of(2025,5,5))
                .cardLimit(100000D)
                .remainingUntilLimit(100000D)
                .cardStatus(AccountStatus.ACTIVE).build();
        var card2 = Card.builder()
                .id(3L).ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("5987632154786255")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,5))
                .expiryDate(LocalDate.of(2025,5,5))
                .cardLimit(100000D)
                .remainingUntilLimit(100000D)
                .cardStatus(AccountStatus.ACTIVE).build();
        var card3 = Card.builder()
                .id(4L).ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("5987632154786256")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,5))
                .expiryDate(LocalDate.of(2025,5,5))
                .cardLimit(100000D)
                .remainingUntilLimit(100000D)
                .cardStatus(AccountStatus.ACTIVE).build();

        List<Card>list = new ArrayList<>();
        list.add(card1);
        list.add(card2);
        list.add(card3);
        return list;
    }
    List<AccountDto> getAllAccountsForUserByIdValidationException(){
        List<AccountDto> list = new ArrayList<>();
        AccountDto acc1 = new AccountDto(1L,"36488031",1L,20000D,"Savings",3L
                ,"RSD",AccountStatus.ACTIVE,LocalDate.now(),LocalDate.now().plusYears(5));


        list.add(acc1);
        return list;
    }
    List<AccountDto> getAllAccountsForUserById(){
        List<AccountDto> list = new ArrayList<>();
        AccountDto acc1 = new AccountDto(1L,"36488030",1L,20000D,"Savings",3L
                ,"RSD",AccountStatus.ACTIVE,LocalDate.now(),LocalDate.now().plusYears(5));


        list.add(acc1);
        return list;
    }


    @Test
    void payWithCardSuccessfully(){
        //given
        var cardPaymentDto =
                new CardPaymentDto(1L,"5478512365",
                        "4587886921",20000D,"RSD");

        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2025,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(50000D)
                .lastLimitDate(LocalDate.of(2023,7,9))
                .cardStatus(AccountStatus.ACTIVE)
                .cardPayments(new ArrayList<>()).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentService.makePayment(any(PaymentCreateDto.class))).thenReturn(getPaymentDto());
        when(cardRepository.save(any(Card.class))).thenReturn(any(Card.class));

        //when
        var result = cardService.payWithCard(cardPaymentDto);
        //then
        assertEquals(1,result.getId());
        assertEquals(1,result.getOwnerId());
        assertEquals(1,result.getCardPayments().size());

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verify(paymentService, times(1)).makePayment((any(PaymentCreateDto.class)));
        verify(cardRepository, times(1)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
        verifyNoMoreInteractions(paymentService);

    }

    @Test
    void payWithCardExceededLimit(){
        //given
        var cardPaymentDto =
                new CardPaymentDto(1L,"5478512365",
                        "4587886921",200000D,"RSD");

        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2025,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(50000D)
                .lastLimitDate(LocalDate.of(2023,7,9))
                .cardStatus(AccountStatus.ACTIVE)
                .cardPayments(new ArrayList<>()).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));


        //when
        //then
        assertThrows(ValidationException.class,()->cardService.payWithCard(cardPaymentDto));
        verify(cardRepository, times(1)).findById((any(Long.class)));
        verifyNoMoreInteractions(cardRepository);

    }
    @Test
    void payWithCardInvalidTransactionCurrencies(){
        //given
        var cardPaymentDto =
                new CardPaymentDto(1L,"5478512365",
                        "4587886921",1000D,"USD");

        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("USD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2025,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(50000D)
                .lastLimitDate(LocalDate.of(2023,7,9))
                .cardStatus(AccountStatus.ACTIVE)
                .cardPayments(new ArrayList<>()).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));


        //when
        //then
        assertThrows(ValidationException.class,()->cardService.payWithCard(cardPaymentDto));
        verify(cardRepository, times(1)).findById((any(Long.class)));
        verifyNoMoreInteractions(cardRepository);

    }
    @Test
    void payWithCardSuccessfullyDifferentCurrency(){
        //given
        var cardPaymentDto =
                new CardPaymentDto(1L,"36488030",
                        "4587886921",100D,"USD");

        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2025,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(50000D)
                .lastLimitDate(LocalDate.of(2023,7,9))
                .cardStatus(AccountStatus.ACTIVE)
                .cardPayments(new ArrayList<>()).cardConversions(new ArrayList<>()).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(currencyExchangeService.convertMoney(any())).thenReturn(getConversionTransferConfirmDto());
        when(currencyExchangeService.confirmConversionTransfer(any(),anyBoolean())).thenReturn(getConversionTransferDto());

        when(cardRepository.save(any(Card.class))).thenReturn(any(Card.class));

        //when
        var result = cardService.payWithCard(cardPaymentDto);
        //then
        assertEquals(1,result.getId());
        assertEquals(1,result.getOwnerId());
        assertEquals(0,result.getCardPayments().size());
        assertEquals(1,result.getCardConversions().size());

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verify(currencyExchangeService, times(1)).convertMoney(any(ConversionTransferCreateDto.class));
        verify(currencyExchangeService, times(1)).confirmConversionTransfer(any(ConversionTransferConfirmDto.class),anyBoolean());
        verify(cardRepository, times(1)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
        verifyNoMoreInteractions(currencyExchangeService);

    }

    ConversionTransferConfirmDto getConversionTransferConfirmDto(){
        return new ConversionTransferConfirmDto("36488030"
                ,"4587886921","RSD/USD"
                , 100D,1060D,106.98767205418449D,10D);
    }
    ConversionTransferDto getConversionTransferDto(){
        return new ConversionTransferDto(1L,1L,"36488030"
                ,"4587886921","RSD/USD",LocalDateTime.now()
                , 100D,1060D,106.98767205418449D,10D);
    }
    PaymentDto getPaymentDto(){
        return PaymentDto.builder().id(1L).senderId(1L)
                .receiverName("Nepoznat primalac")
                .senderAccountNumber("5478512365")
                .receiverAccountNumber("4587886921")
                .amount(20000D)
                .paymentTime(LocalDateTime.now())
                .referenceNumber(null)
                .paymentNumber("184")
                .paymentPurpose("Transakcija karticom")
                .currencySymbol("RSD").build();
    }
    @Test
    void findCardByIdSuccessfully(){
        //given
        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        //when
        var result = cardService.findCardById(1L);

        //then
        assertEquals(1, result.getId());
        assertEquals("36488030", result.getAccountNumber());
        assertEquals("380774991", result.getCardNumber());

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void findAllCardsForAccountSuccessfully(){
        //given

        when(cardRepository.findAllByAccountNumber("36488030")).thenReturn(getCardsForAccount());

        //when
        var result = cardService.findAllCardsForAccount("36488030");

        //then
        assertEquals(3, result.size());

        verify(cardRepository, times(1)).findAllByAccountNumber((any(String.class)));
        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void findAllCardsForLoggedInUserSuccessfully(){
        //given
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user1));
        when(cardRepository.findAllByOwnerId(1L)).thenReturn(getCardsForAccount());
        //when
        var result = cardService.findAllCardsForLoggedInUser();
        //then
        assertEquals(3, result.size());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(cardRepository, times(1)).findAllByOwnerId(anyLong());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(cardRepository);
    }
    @Test
    void findAllCardsForLoggedInUserNotFoundException(){
        //given
        var user1 = BankUser.builder().id(1L).email("email@gmail.com").firstName("Test").lastName("Test").build();
        var authenticationToken =
                new UsernamePasswordAuthenticationToken("test", null, null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> cardService.findAllCardsForLoggedInUser());

        verify(userRepository, times(1)).findByEmail(anyString());
        verify(cardRepository, times(0)).findAllByOwnerId(anyLong());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(cardRepository);
    }
    @Test
    void updateCardLimitSuccessfully(){
        //given
        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        //when
        var result = cardService.updateCardLimit(1L,50000D);

        //then
        assertEquals(1, result.getId());
        assertEquals(50000D, result.getCardLimit());
        assertEquals(40000D, result.getRemainingUntilLimit());

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verify(cardRepository, times(1)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void updateCardLimitNotFoundException(){
        //given
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> cardService.updateCardLimit(1L,50000D));

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verify(cardRepository, times(0)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void updateCardStatusSuccessfully(){
        //given
        var card = Card.builder()
                .id(1L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();
        var card2 = Card.builder()
                .id(2L)
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774992")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.INACTIVE).build();

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card2));
        //when
        var result = cardService.updateCardStatus(1L);
        var result1  = cardService.updateCardStatus(2L);

        //then
        assertEquals(1L, result.getId());
        assertEquals(AccountStatus.INACTIVE, result.getCardStatus());

        assertEquals(2L, result1.getId());
        assertEquals(AccountStatus.ACTIVE, result1.getCardStatus());

        verify(cardRepository, times(2)).findById((any(Long.class)));
        verify(cardRepository, times(2)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
    }

    @Test
    void updateCardStatusNotFoundException(){
        //given
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        //when
        //then
        assertThrows(NotFoundException.class, () -> cardService.updateCardLimit(1L,50000D));

        verify(cardRepository, times(1)).findById((any(Long.class)));
        verify(cardRepository, times(0)).save((any(Card.class)));

        verifyNoMoreInteractions(cardRepository);
    }
    List<Card> getCardsForAccount(){
        var card1 = Card.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774991")
                .cvvCode(695)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();
        var card2 = Card.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774992")
                .cvvCode(696)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();
        var card3 = Card.builder()
                .ownerId(1L)
                .accountNumber("36488030")
                .cardNumber("380774993")
                .cvvCode(696)
                .cardName("Debit")
                .cardType(CardType.DEBIT)
                .cardCurrencySymbol("RSD")
                .creationDate(LocalDate.of(2020,5,9))
                .expiryDate(LocalDate.of(2026,5,9))
                .cardLimit(100000D)
                .remainingUntilLimit(90000D)
                .lastLimitDate(LocalDate.of(2023,6,9))
                .cardStatus(AccountStatus.ACTIVE).build();

        return List.of(card1,card2,card3);
    }
}
