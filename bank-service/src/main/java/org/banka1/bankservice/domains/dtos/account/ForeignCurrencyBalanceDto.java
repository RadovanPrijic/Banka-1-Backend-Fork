package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForeignCurrencyBalanceDto {

    private Long id;
    private String foreignCurrencyCode;
    private Double accountBalance;

}
