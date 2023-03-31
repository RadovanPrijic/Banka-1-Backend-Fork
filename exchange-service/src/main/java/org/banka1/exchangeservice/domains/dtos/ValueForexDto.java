package org.banka1.exchangeservice.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueForexDto {

    private String datetime;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
}
