package org.banka1.exchangeservice.domains.dtos;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CurrencyCsvBean {

    @CsvBindByPosition(position = 0)
    private String currencyCode;

    @CsvBindByPosition(position = 1)
    private String currencyName;

}
