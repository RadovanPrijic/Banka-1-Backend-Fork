package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDto {
    private String symbol;
    private Double price;
    private Double volume;
    private Double change;
    private Double changeInPercentage;
    private Long lastRefreshedInMillis;
}
