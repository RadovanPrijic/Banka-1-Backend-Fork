package org.banka1.bankservice.domains.dtos.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardPaymentDto {

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long cardId;

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @DecimalMin(value="0.01", message="decimalField: positive number, min 0.01 is required")
    private Double amount;

    @NotBlank
    private String currencySymbol;

}
