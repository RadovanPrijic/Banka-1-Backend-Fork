package org.banka1.bankservice.domains.dtos.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.bankservice.domains.entities.account.AccountType;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForeignCurrencyAccountDto extends AccountDto {

    private AccountType accountType;
    private Double interestRate;
    private Double maintenanceCost;
    private List<ForeignCurrencyBalanceDto> foreignCurrencyBalances;

}
