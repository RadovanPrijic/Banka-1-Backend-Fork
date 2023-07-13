package org.banka1.bankservice.domains.dtos.currency_exchange;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlaskResponse {

    @JsonProperty("exchange_rate")
    private Double exchangeRate;

}
