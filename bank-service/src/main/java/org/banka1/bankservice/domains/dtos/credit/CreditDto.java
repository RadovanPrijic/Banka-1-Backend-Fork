package org.banka1.bankservice.domains.dtos.credit;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditDto {

    private Long id;
    private Long clientId;
    private String accountNumber;
    private String creditName;
    private Double creditAmount;
    private Integer amortisationLength;
    private Double interestRate;
    private Double creditInstallmentAmount;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate creationDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate dueDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate nextInstallmentFirstDate;

    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate nextInstallmentLastDate;

    private Double leftToPay;
    private String currencyCode;


}
