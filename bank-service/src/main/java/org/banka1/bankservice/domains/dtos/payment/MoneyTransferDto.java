package org.banka1.bankservice.domains.dtos.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MoneyTransferDto {

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @DecimalMin(value="0.01", message="decimalField: positive number, min 0.0 is required")
    private Double amount;

    @NotBlank
    private String currencySymbol;

}
