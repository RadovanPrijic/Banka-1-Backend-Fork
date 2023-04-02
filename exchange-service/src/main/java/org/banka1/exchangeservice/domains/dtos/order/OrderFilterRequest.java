package org.banka1.exchangeservice.domains.dtos.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.BooleanBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.entities.QOrder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderFilterRequest {

    private OrderStatus orderStatus;
    private Boolean done;
    private Long userId;

    @JsonIgnore
    QOrder qOrder = QOrder.order;

    @JsonIgnore
    public BooleanBuilder getPredicate(){
        BooleanBuilder predicate = new BooleanBuilder();

        if(orderStatus != null){
            predicate.and(qOrder.orderStatus.eq(orderStatus));
        }
        if(done != null){
            predicate.and(qOrder.done.eq(done));
        }
        if(userId != null){
            predicate.and(qOrder.userId.eq(userId));
        }

        return predicate;
    }
}
