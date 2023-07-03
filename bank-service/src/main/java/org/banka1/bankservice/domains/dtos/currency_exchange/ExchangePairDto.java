package org.banka1.bankservice.domains.dtos.currency_exchange;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangePairDto {

    private String exchangePairSymbol;
    private Double exchangeRate;

}
