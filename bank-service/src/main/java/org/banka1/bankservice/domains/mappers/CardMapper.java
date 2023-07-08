package org.banka1.bankservice.domains.mappers;

import org.banka1.bankservice.domains.dtos.card.CardCreateDto;
import org.banka1.bankservice.domains.dtos.card.CardDto;
import org.banka1.bankservice.domains.entities.card.Card;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    CardDto cardToCardDto(Card card);
    Card cardCreateDtoToCard(CardCreateDto cardCreateDto);

}
