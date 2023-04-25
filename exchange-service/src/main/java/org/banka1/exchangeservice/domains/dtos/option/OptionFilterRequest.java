package org.banka1.exchangeservice.domains.dtos.option;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.QOption;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OptionFilterRequest {

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate expirationDate;
    private String symbol;

    @JsonIgnore
    QOption qOption = QOption.option;

    @JsonIgnore
    public BooleanBuilder getPredicate() {
        BooleanBuilder predicate = new BooleanBuilder();
        if(expirationDate != null) {
            predicate.and(qOption.expirationDate.eq(expirationDate));
        }
        if(symbol != null) {
            predicate.and(qOption.symbol.containsIgnoreCase(symbol));
        }

        return predicate;
    }
}
