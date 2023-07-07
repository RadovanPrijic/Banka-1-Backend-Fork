package org.banka1.bankservice.domains.dtos.credit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.credit.CreditRequestStatus;
import org.banka1.bankservice.domains.entities.credit.CreditType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreditRequestCreateDto {

    @NotBlank
    private String accountNumber;

    @NotNull
    private CreditType creditType;

    @DecimalMin(value="100.0", message="decimalField: positive number, min 100.0 is required")
    private Double creditAmount;

    @DecimalMin(value="0.001", message="decimalField: positive number, min 0.001 is required")
    private Double interestRate;

    @NotBlank
    private String creditPurpose;

    @DecimalMin(value="100.0", message="decimalField: positive number, min 100.0 is required")
    private Double monthlySalary;

    @NotNull
    private boolean employedFullTime;

    @Min(value=1, message="numericField: positive number, min 1 is required")
    private Integer currentEmploymentDuration;

    @NotBlank
    private String educationLevel;

    @Min(value=1, message="numericField: positive number, min 1 is required")
    private Integer monthsToPayOff;

    @NotBlank
    private String branchOffice;

    @NotBlank
    private String phoneNumber;

}
