package org.banka1.exchangeservice.domains.dtos.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.ListingType;
import org.banka1.exchangeservice.domains.entities.OrderAction;
import org.banka1.exchangeservice.domains.entities.OrderType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    private String symbol;
    private ListingType listingType;
    private Integer quantity;
    private OrderAction orderAction;
    private OrderType orderType;
    private Double limitValue;
    private Double stopValue;
    private boolean allOrNoneFlag;
    private boolean marginFlag;

}
