package org.banka1.exchangeservice.domains.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountDto {

    private Long id;
    private String currencyCode;
    private Double accountBalance;
    private Double dailyLimit;

}
