package org.banka1.exchangeservice.domains.dtos.forex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.QForex;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForexFilterRequest {
    private String fromCurrencyCode;
    private String toCurrencyCode;
    @JsonIgnore
    QForex qForex = QForex.forex;
    @JsonIgnore
    public BooleanBuilder getPredicate(){
        BooleanBuilder predicate = new BooleanBuilder();
        if(fromCurrencyCode != null){
            predicate.and(qForex.fromCurrency.currencyCode.containsIgnoreCase(fromCurrencyCode));
        }
        if(toCurrencyCode != null){
            predicate.and(qForex.toCurrency.currencyCode.containsIgnoreCase(toCurrencyCode));
        }
        return predicate;
    }
}
