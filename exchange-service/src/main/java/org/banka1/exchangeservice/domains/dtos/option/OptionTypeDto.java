package org.banka1.exchangeservice.domains.dtos.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionTypeDto {
    private String contractSymbol;
    private Double strike;
    private String currency;
    private Double lastPrice;
    private Double change;
    private Double percentChange;
    private Double volume;
    private Double openInterest;
    private Double bid;
    private Double ask;
    private String contractSize;
    private Long expiration;
    private Long lastTradeDate;
    private Double impliedVolatility;
    private Boolean inTheMoney;
}
