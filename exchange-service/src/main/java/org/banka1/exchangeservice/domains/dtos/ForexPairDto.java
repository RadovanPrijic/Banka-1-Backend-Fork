package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexPairDto {
    private String currency_base;
    private String currency_group;
    private String currency_quote;
    private String symbol;
}
