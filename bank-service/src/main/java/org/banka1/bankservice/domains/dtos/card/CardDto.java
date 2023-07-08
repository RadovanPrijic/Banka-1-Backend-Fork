package org.banka1.bankservice.domains.dtos.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentDto;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.card.CardType;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {

    private Long id;
    private Long ownerId;
    private String accountNumber;

    private String cardNumber;
    private Integer cvvCode;
    private String cardName;
    private CardType cardType;
    private String cardCurrencySymbol;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate creationDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate expiryDate;

    private Double cardLimit;
    private Double remainingUntilLimit;
    private AccountStatus cardStatus;

    private List<PaymentDto> cardPayments;
    private List<ConversionTransferDto> cardConversions;

}
