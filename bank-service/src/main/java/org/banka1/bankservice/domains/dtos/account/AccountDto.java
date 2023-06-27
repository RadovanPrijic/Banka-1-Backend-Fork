package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.account.AccountStatus;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Long id;
    private String accountNumber;
    private Long ownerId;
    private Double accountBalance;
    private String accountName;
    private Long employeeId;
    private String defaultCurrencyCode;
    private AccountStatus accountStatus;
    private LocalDate creationDate;
    private LocalDate expiryDate;

}
