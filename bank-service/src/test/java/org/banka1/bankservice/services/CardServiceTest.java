package org.banka1.bankservice.services;

import org.banka1.bankservice.repositories.CardRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.mock;

public class CardServiceTest {
    private CardRepository cardRepository;
    private UserRepository userRepository;
    private AccountService accountService;
    private PaymentService paymentService;
    private CurrencyExchangeService currencyExchangeService;

    private CardService cardService;
    @BeforeEach
    void setUp() {
        this.cardRepository = mock(CardRepository.class);
        this.userRepository = mock(UserRepository.class);
        this.accountService = mock(AccountService.class);
        this.paymentService = mock(PaymentService.class);
        this.currencyExchangeService = mock(CurrencyExchangeService.class);

        this.cardService = new CardService(cardRepository, userRepository, accountService,paymentService,currencyExchangeService);
    }
}
