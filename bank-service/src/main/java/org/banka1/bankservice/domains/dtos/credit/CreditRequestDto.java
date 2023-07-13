package org.banka1.bankservice.domains.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.credit.CreditRequestStatus;
import org.banka1.bankservice.domains.entities.credit.CreditType;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditRequestDto {

    private Long id;
    private String clientEmail;
    private String accountNumber;
    private CreditRequestStatus creditRequestStatus;
    private CreditType creditType;
    private Double creditAmount;
    private Double interestRate;
    private String creditPurpose;
    private Double monthlySalary;
    private boolean employedFullTime;
    private Integer currentEmploymentDuration;
    private String educationLevel;
    private Integer monthsToPayOff;
    private String branchOffice;
    private String phoneNumber;

}
