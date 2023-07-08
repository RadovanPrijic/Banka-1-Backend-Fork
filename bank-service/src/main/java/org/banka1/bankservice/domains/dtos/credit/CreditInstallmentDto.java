package org.banka1.bankservice.domains.dtos.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditInstallmentDto {

    private Long id;
    private Long creditId;
    private Long clientId;

    @JsonFormat(pattern="dd-MM-yyyy HH:mm:ss")
    private LocalDateTime creditInstallmentPaymentTime;

    private Double creditInstallmentAmount;
    private Double interestRateAmount;

}
