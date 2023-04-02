package org.banka1.exchangeservice.domains.dtos.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDtoFlask {
    private String symbol;
    @JsonProperty("last_refreshed")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date lastRefreshed;
    @JsonProperty("time_series")
    private List<StockDtoFlask> timeSeries;
}
