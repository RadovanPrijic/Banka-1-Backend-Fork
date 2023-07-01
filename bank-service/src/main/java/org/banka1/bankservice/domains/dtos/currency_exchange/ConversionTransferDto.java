package org.banka1.bankservice.domains.dtos.currency_exchange;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionTransferDto {

    private Long id;
    private Long senderId;
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private String exchangePairSymbol;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime conversionTime;

    private Double amount;
    private Double convertedAmount;
    private Double exchangeRate;
    private Double commissionFee;

}
