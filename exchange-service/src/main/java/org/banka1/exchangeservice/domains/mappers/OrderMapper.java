package org.banka1.exchangeservice.domains.mappers;

import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.entities.Order;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "allOrNone", source = "allOrNoneFlag")
    @Mapping(target = "margin", source = "marginFlag")
    @Mapping(target = "listingSymbol", source = "symbol")
    void updateOrderFromOrderRequest(@MappingTarget Order order, OrderRequest orderRequest);
}
