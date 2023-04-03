package org.banka1.exchangeservice.domains.dtos.forex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexResponseTimeSeriesFlask {

    @JsonProperty("from_currency")
    private String fromCurrency;
    @JsonProperty("to_currency")
    private String toCurrency;
    @JsonProperty("last_refreshed")
    private String lastRefreshed;
    @JsonProperty("time_series")
    private List<ForexTimeSeriesFlask> timeSeries;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ForexTimeSeriesFlask {
        private String date;
        private Double open;
        private Double close;
        private Double high;
        private Double low;
    }
}
