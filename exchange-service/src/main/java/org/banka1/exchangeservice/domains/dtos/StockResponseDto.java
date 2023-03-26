package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockResponseDto {

    private List<StockDto> data;
    private String status;
}
