package org.banka1.bankservice.domains.dtos.card;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.payment.PaymentDto;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.card.CardType;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardCreateDto {

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long ownerId;

    @NotBlank
    private String accountNumber;

    @Min(value=100, message="numericField: positive number, min 100 is required")
    @Max(value=999, message="numericField: positive number, max 999 is required")
    private Integer cvvCode;

    @NotBlank
    private String cardName;

    @NotNull
    private CardType cardType;

    @NotBlank
    private String cardCurrencySymbol;

    @DecimalMin(value="50.0", message="decimalField: positive number, min 50.0 is required")
    private Double cardLimit;

    @NotNull
    private AccountStatus cardStatus;

}
