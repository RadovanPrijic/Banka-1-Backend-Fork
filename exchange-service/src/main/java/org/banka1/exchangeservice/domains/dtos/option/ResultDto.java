package org.banka1.exchangeservice.domains.dtos.option;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDto {
    private String underlyingSymbol;
    @JsonIgnore
    private List<Double> expirationDates;
    @JsonIgnore
    private List<Double> strikes;
    @JsonIgnore
    private Boolean hasMiniOptions;
    @JsonIgnore
    private String quote;
    private List<OptionResponseDto> options;
}
