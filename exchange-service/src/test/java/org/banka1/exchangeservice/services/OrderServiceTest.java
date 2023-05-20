package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;
import org.banka1.exchangeservice.domains.entities.Option;
import org.banka1.exchangeservice.domains.entities.OptionBet;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.OptionBetRepository;
import org.banka1.exchangeservice.repositories.OptionRepository;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class OrderServiceTest {

    private OptionRepository optionRepository;
    private OptionBetRepository optionBetRepository;
    private OrderRepository orderRepository;
    private StockRepository stockRepository;
    private ForexRepository forexRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private OrderService orderService;
    private ForexService forexService;
    private StockService stockService;

    @BeforeEach
    void setUp() {
        this.optionRepository = mock(OptionRepository.class);
        this.optionBetRepository = mock(OptionBetRepository.class);
        this.orderRepository = mock(OrderRepository.class);
        this.stockRepository = mock(StockRepository.class);
        this.forexRepository = mock(ForexRepository.class);
        this.forexService = mock(ForexService.class);
        this.stockService = mock(StockService.class);
        this.orderService = new OrderService(orderRepository, forexRepository, stockRepository, optionBetRepository,
                optionRepository, forexService, stockService, new JwtUtil());
    }

   @Test
    void getAllOptionsTest() {
       Option option = Option.builder()
               .id(1L)
               .bid(111D)
               .strike(123D)
               .build();

       when(optionRepository.findAll()).thenReturn(List.of(option));

       var result = orderService.getAllOptions();

       assertEquals(1, result.size());

       verify(optionRepository, times(1)).findAll();
       verifyNoMoreInteractions(optionRepository);
    }

}
