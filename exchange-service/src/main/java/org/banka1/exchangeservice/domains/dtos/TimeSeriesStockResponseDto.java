package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSeriesStockResponseDto {

    private MetaStockDto meta;
    private List<ValueStockDto> values;
    private String status;
}
