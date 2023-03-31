package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetaForexDto {

    private String symbol;
    private String interval;
    private String currency_base;
    private String currency_quote;
    private String type;
}
