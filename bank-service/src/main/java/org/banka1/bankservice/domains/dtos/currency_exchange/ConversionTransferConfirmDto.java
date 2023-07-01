package org.banka1.bankservice.domains.dtos.currency_exchange;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionTransferConfirmDto {

    @NotBlank
    private String senderAccountNumber;

    @NotBlank
    private String receiverAccountNumber;

    @NotBlank
    private String exchangePairSymbol;

    @DecimalMin(value="0.01", message="decimalField: positive number, min 0.0 is required")
    private Double amount;

    @DecimalMin(value="0.01", message="decimalField: positive number, min 0.0 is required")
    private Double convertedAmount;

    @DecimalMin(value="0.000001", message="decimalField: positive number, min 0.0 is required")
    private Double exchangeRate;

    @DecimalMin(value="0.0", message="decimalField: positive number, min 0.0 is required")
    private Double commissionFee;

}
