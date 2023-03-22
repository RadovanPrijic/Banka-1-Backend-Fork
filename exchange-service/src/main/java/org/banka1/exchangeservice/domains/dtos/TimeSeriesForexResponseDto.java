package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesForexResponseDto {

    private MetaForexDto meta;
    private List<ValueForexDto> values;
    private String status;
}
