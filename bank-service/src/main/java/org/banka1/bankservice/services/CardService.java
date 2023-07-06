package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.account.AccountDto;
import org.banka1.bankservice.domains.dtos.card.CardCreateDto;
import org.banka1.bankservice.domains.dtos.card.CardDto;
import org.banka1.bankservice.domains.dtos.card.CardPaymentDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferConfirmDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferCreateDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentCreateDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentDto;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.card.Card;
import org.banka1.bankservice.domains.entities.user.BankUser;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.exceptions.ValidationException;
import org.banka1.bankservice.domains.mappers.CardMapper;
import org.banka1.bankservice.domains.mappers.CurrencyExchangeMapper;
import org.banka1.bankservice.domains.mappers.PaymentMapper;
import org.banka1.bankservice.repositories.CardRepository;
import org.banka1.bankservice.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final AccountService accountService;
    private final PaymentService paymentService;
    private final CurrencyExchangeService currencyExchangeService;

    public CardService(CardRepository cardRepository, UserRepository userRepository,
                       AccountService accountService, PaymentService paymentService, CurrencyExchangeService currencyExchangeService) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.paymentService = paymentService;
        this.currencyExchangeService = currencyExchangeService;
    }

    public CardDto createCard(CardCreateDto cardCreateDto) {
        validateCardCreation(cardCreateDto);

        Card card = CardMapper.INSTANCE.cardCreateDtoToCard(cardCreateDto);
        card.setCardNumber(generateCardNumber(17));
        card.setCreationDate(LocalDate.now());
        card.setExpiryDate(card.getCreationDate().plusYears(5));
        card.setRemainingUntilLimit(card.getCardLimit());
        card.setLastLimitDate(card.getCreationDate());
        card.setCardStatus(AccountStatus.ACTIVE);

        cardRepository.save(card);

        return CardMapper.INSTANCE.cardToCardDto(card);
    }

    public CardDto payWithCard(CardPaymentDto cardPaymentDto) {
        Card card = cardRepository.findById(cardPaymentDto.getCardId()).orElseThrow(() -> new NotFoundException("Card has not been found."));

        if(LocalDate.now().isAfter(card.getLastLimitDate()))
            card.setRemainingUntilLimit(card.getCardLimit()); // Resetuj dnevni limit ako je novi dan

        if(card.getRemainingUntilLimit() < cardPaymentDto.getAmount())
            throw new ValidationException("This card payment would have exceeded your daily limit, therefore it was not executed.");
        else
            card.setRemainingUntilLimit(card.getRemainingUntilLimit() - cardPaymentDto.getAmount());

        if(card.getCardCurrencySymbol().equals("RSD") && cardPaymentDto.getCurrencySymbol().equals("RSD")){ // Sa dinarskog na dinarski
            PaymentCreateDto paymentCreateDto = new PaymentCreateDto(
                    "Nepoznat primalac",
                    cardPaymentDto.getSenderAccountNumber(),
                    cardPaymentDto.getReceiverAccountNumber(),
                    cardPaymentDto.getAmount(),
                    null,
                    "184",
                    "Transakcija karticom"
            );

            PaymentDto paymentDto = paymentService.makePayment(paymentCreateDto);
            card.getCardPayments().add(PaymentMapper.INSTANCE.paymentDtoToPayment(paymentDto));

            cardRepository.save(card);

        } else if(card.getCardCurrencySymbol().equals("RSD") || cardPaymentDto.getCurrencySymbol().equals("RSD")){ // Konverzija RSD/XYZ ili XYZ/RSD
            String cardTransactionExchangePair = card.getCardCurrencySymbol() + "/" + cardPaymentDto.getCurrencySymbol();

            ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto(
                    cardPaymentDto.getSenderAccountNumber(),
                    cardPaymentDto.getReceiverAccountNumber(),
                    cardTransactionExchangePair,
                    cardPaymentDto.getAmount()
            );

            ConversionTransferConfirmDto conversionTransferConfirmDto = currencyExchangeService.convertMoney(conversionTransferCreateDto);
            ConversionTransferDto conversionTransferDto = currencyExchangeService.confirmConversionTransfer(conversionTransferConfirmDto, true);
            card.getCardConversions().add(CurrencyExchangeMapper.INSTANCE.conversionTransferDtoToConversionTransfer(conversionTransferDto));

            cardRepository.save(card);
        } else
            throw new ValidationException("Your card transaction has invalid transaction currencies.");

        return CardMapper.INSTANCE.cardToCardDto(card);
    }

    public CardDto findCardById(Long id) {
        Optional<Card> card = cardRepository.findById(id);

        return card.map(CardMapper.INSTANCE::cardToCardDto).orElseThrow(() -> new NotFoundException("Card has not been found."));
    }

    public List<CardDto> findAllCardsForAccount(String accountNumber) {

        return cardRepository.findAllByAccountNumber(accountNumber).stream().map(CardMapper.INSTANCE::cardToCardDto).collect(Collectors.toList());
    }

    public List<CardDto> findAllCardsForLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        BankUser user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User has not been found."));
        Long userId = user.getId();

        return cardRepository.findAllByOwnerId(userId).stream().map(CardMapper.INSTANCE::cardToCardDto).collect(Collectors.toList());
    }

    public CardDto updateCardLimit(Long id, Double newLimit) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card has not been found."));

        card.setRemainingUntilLimit(newLimit - (card.getCardLimit() - card.getRemainingUntilLimit()));
        card.setCardLimit(newLimit);

        cardRepository.save(card);

        return CardMapper.INSTANCE.cardToCardDto(card);
    }

    public CardDto updateCardStatus(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(() -> new NotFoundException("Card has not been found."));

        if(card.getCardStatus() == AccountStatus.ACTIVE)
            card.setCardStatus(AccountStatus.INACTIVE);
        else
            card.setCardStatus(AccountStatus.ACTIVE);

        cardRepository.save(card);

        return CardMapper.INSTANCE.cardToCardDto(card);
    }

    public void validateCardCreation(CardCreateDto cardCreateDto) {
        BankUser user = userRepository.findById(cardCreateDto.getOwnerId()).orElseThrow(() -> new NotFoundException("User has not been found."));

        boolean accountFoundInUserAccounts = false;
        List<AccountDto> userAccounts = accountService.findAllAccountsForUserById(user.getId());
        for(AccountDto account : userAccounts ) {
            if (account.getAccountNumber().equals(cardCreateDto.getAccountNumber())) {
                accountFoundInUserAccounts = true;
                break;
            }
        }

        if(!accountFoundInUserAccounts)
            throw new ValidationException("This account does not belong to the user specified in card creation form.");

        List<CardDto> userAccountCards = findAllCardsForAccount(cardCreateDto.getAccountNumber());
        if(userAccountCards.size() == 3)
            throw new ValidationException("User has already reached a maximum of 3 cards for one bank account.");
    }

    public static String generateCardNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 1; i < length; i++) {
            int digit = random.nextInt(10);
            sb.append(digit);
        }

        return String.valueOf(sb);
    }

}
