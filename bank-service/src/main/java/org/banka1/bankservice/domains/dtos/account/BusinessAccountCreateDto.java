package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.account.AccountStatus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessAccountCreateDto {

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long ownerId;

    @NotBlank
    private String accountName;

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long employeeId;

    @Min(value=0, message="numericField: positive number, min 0 is required")
    private Long companyId;

}
