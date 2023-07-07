package org.banka1.bankservice.domains.dtos.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InterestRatePaymentDto {

    private Long id;
    private Long creditId;
    private Long clientId;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime interestRatePaymentTime;

    private Double paymentAmount;
    private Double interestRateAmount;

}
