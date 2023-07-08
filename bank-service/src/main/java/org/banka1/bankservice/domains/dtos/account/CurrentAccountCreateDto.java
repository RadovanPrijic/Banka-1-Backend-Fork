package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.account.AccountStatus;
import org.banka1.bankservice.domains.entities.account.AccountType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccountCreateDto {

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long ownerId;

    @NotBlank
    private String accountName;

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long employeeId;

    @NotNull
    private AccountType accountType;

    @DecimalMin(value="0.0", message="decimalField: positive number, min 0.0 is required")
    private Double interestRate;

    @DecimalMin(value="0.0", message="decimalField: positive number, min 0.0 is required")
    private Double maintenanceCost;

}
