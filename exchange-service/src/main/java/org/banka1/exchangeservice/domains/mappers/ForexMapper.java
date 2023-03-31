package org.banka1.exchangeservice.domains.mappers;

import org.banka1.exchangeservice.domains.dtos.forex.ForexResponseExchangeFlask;
import org.banka1.exchangeservice.domains.entities.Forex;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ForexMapper {

    ForexMapper INSTANCE = Mappers.getMapper(ForexMapper.class);


    @Mapping(target = "fromCurrency", ignore = true)
    @Mapping(target = "toCurrency", ignore = true)
    @Mapping(target = "lastRefresh", ignore = true)
    void updateForexFromForexResponseExchangeFlask(@MappingTarget Forex forex, ForexResponseExchangeFlask forexResponseExchangeFlask);
}
