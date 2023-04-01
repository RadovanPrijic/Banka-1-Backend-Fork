package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.mappers.OrderMapper;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderService {

    private OrderRepository orderRepository;



    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public void makeOrder(OrderRequest orderRequest, Long userId){
        //todo if user nije prekoracio kolicinu za danas
        Order order = new Order();
        // todo setUserEmail
        order.setOrderStatus(OrderStatus.ON_HOLD);
        order.setUserId(userId);
        OrderMapper.INSTANCE.updateOrderFromOrderRequest(order, orderRequest);

        orderRepository.save(order);
    }

}
