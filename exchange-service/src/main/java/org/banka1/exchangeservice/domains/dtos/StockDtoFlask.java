package org.banka1.exchangeservice.domains.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDtoFlask {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;
}
