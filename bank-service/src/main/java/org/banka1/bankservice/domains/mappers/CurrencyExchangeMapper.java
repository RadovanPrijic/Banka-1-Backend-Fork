package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferConfirmDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ExchangePairDto;
import org.banka1.bankservice.domains.entities.currency_exchange.ConversionTransfer;
import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CurrencyExchangeMapper {

    CurrencyExchangeMapper INSTANCE = Mappers.getMapper(CurrencyExchangeMapper.class);

    ExchangePairDto exchangePairToExchangePairDto(ExchangePair exchangePair);
    ConversionTransferDto conversionTransferToConversionTransferDto(ConversionTransfer conversionTransfer);
    ConversionTransfer conversionTransferConfirmDtoToConversionTransfer(ConversionTransferConfirmDto conversionTransferConfirmDto);

}
