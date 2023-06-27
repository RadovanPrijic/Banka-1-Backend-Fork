package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.bankservice.domains.entities.account.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CurrencyMapper {

    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyCsvBean currencyToCurrencyCsvBean(Currency currency);

}
