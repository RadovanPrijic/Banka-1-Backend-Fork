package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest2 extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @Test
    public void getUserDtoFromUserService() {
        UserDto userDto = orderService.getUserDtoFromUserService("Bearer " + getToken());
        Assertions.assertNotNull(userDto);
    }

}
