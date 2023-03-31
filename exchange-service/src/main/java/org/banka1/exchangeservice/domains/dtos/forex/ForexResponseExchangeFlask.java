package org.banka1.exchangeservice.domains.dtos.forex;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexResponseExchangeFlask {

    @JsonProperty("from_currency")
    private String fromCurrency;
    @JsonProperty("to_currency")
    private String toCurrency;
    @JsonProperty("exchange_rate")
    private Double exchangeRate;
    @JsonProperty("bid_price")
    private Double bidPrice;
    @JsonProperty("ask_price")
    private Double askPrice;
    @JsonProperty("last_refresh")
    private String lastRefresh;

}
