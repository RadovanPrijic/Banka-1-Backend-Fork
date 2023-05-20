package org.banka1.exchangeservice;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;

import org.banka1.exchangeservice.domains.entities.*;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.banka1.exchangeservice.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderServiceTest extends IntegrationTest{

    private OrderRepository orderRepository;
    private ForexRepository forexRepository;
    private StockRepository stockRepository;
    private ForexService forexService;
    private StockService stockService;
    private OrderService orderService;

    @Value("${user.service.endpoint}")
    private String userServiceUrl;

    @BeforeEach
    void setUp() {
        this.orderRepository = mock(OrderRepository.class);
        this.forexRepository = mock(ForexRepository.class);
        this.stockRepository = mock(StockRepository.class);
        this.forexService = mock(ForexService.class);
        this.stockService = mock(StockService.class);
        this.orderService = new OrderService(orderRepository, forexRepository, stockRepository, forexService, stockService, new JwtUtil());
    }

    @Test
    public void getUserDtoFromUserServiceTest(){
//        var result = orderService.getUserDtoFromUserService(getToken());
//
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void rejectOrderTest(){
//        Order order = new Order();
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        orderService.rejectOrder(getToken(), 1L);
//
//        verify(orderRepository, times(1)).save(order);
//        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void rejectOrderNotFoundExceptionTest(){
//        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);
//
//        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.rejectOrder(getToken(), 1L));
//        verify(orderRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void approveOrderTest(){
//        Order order = new Order();
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        orderService.approveOrder(getToken(), 1L);
//
//        verify(orderRepository, times(1)).save(order);
//        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.APPROVED);
    }

    @Test
    public void approveOrderNotFoundExceptionTest(){
//        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);
//
//        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.approveOrder(getToken(), 1L));
//        verify(orderRepository, times(1)).findById(1L);
//        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void reduceDailyLimitForUserTest(){
//        double limitDecrease = 10000;
//        var userDtoBeforeLimitDecrease = orderService.getUserDtoFromUserService(getToken());
//
//        orderService.reduceDailyLimitForUser(getToken(), 1L, limitDecrease);
//        var userDtoAfterLimitDecrease = orderService.getUserDtoFromUserService(getToken());
//
//        Assertions.assertEquals(userDtoBeforeLimitDecrease.getBankAccount().getDailyLimit() - limitDecrease,
//                userDtoAfterLimitDecrease.getBankAccount().getDailyLimit());
    }

    @Test
    public void  updateBankAccountBalanceTest() {
//        double accountBalanceToUpdate = 10.00;
//        var userDtoBeforeUpdate = orderService.getUserDtoFromUserService(getToken());
//
//        String url = userServiceUrl + "/users/increase-balance?increaseAccount=" + accountBalanceToUpdate;
//        orderService.updateBankAccountBalance(getToken(), url);
//        var userDtoAfterUpdate = orderService.getUserDtoFromUserService(getToken());
//
//        Assertions.assertEquals(userDtoBeforeUpdate.getBankAccount().getAccountBalance() + accountBalanceToUpdate,
//                userDtoAfterUpdate.getBankAccount().getAccountBalance());
    }

    @Test
    public void calculateThePriceForForexTest(){
//        ListingType listingType = ListingType.FOREX;
//        String symbol = "USD/CAD";
//        Integer qty = 5;
//
//        Forex forex = new Forex();
//        forex.setExchangeRate(1.35);
//
//        when(forexRepository.findBySymbol("USD/CAD")).thenReturn(forex);
//        var result = orderService.calculateThePrice(listingType, symbol, qty);
//
//        Assertions.assertEquals(forex.getExchangeRate() * qty, result);
    }

    @Test
    public void calculateThePriceForStockTest(){
//        ListingType listingType = ListingType.STOCK;
//        String symbol = "AAPL";
//        Integer qty = 5;
//
//        Stock stock = new Stock();
//        stock.setPrice(173.15);
//
//        when(stockRepository.findBySymbol("AAPL")).thenReturn(stock);
//        var result = orderService.calculateThePrice(listingType, symbol, qty);
//
//        Assertions.assertEquals(stock.getPrice() * qty, result);
    }

    @Test
    public void getOrdersTest(){
//        Order order1 = Order.builder().listingType(ListingType.STOCK).listingSymbol("AAPL").quantity(5).build();
//        Order order2 = Order.builder().listingType(ListingType.STOCK).listingSymbol("GOOGL").quantity(10).build();
//        Order order3 = Order.builder().listingType(ListingType.STOCK).listingSymbol("MSFT").quantity(15).build();
//        ArrayList<Order> orders = new ArrayList<>();
//        orders.add(order1);
//        orders.add(order2);
//        orders.add(order3);
//
//        Stock stock1 = new Stock();
//        stock1.setPrice(173.15);
//        when(stockRepository.findBySymbol("AAPL")).thenReturn(stock1);
//
//        Stock stock2 = new Stock();
//        stock2.setPrice(116.57);
//        when(stockRepository.findBySymbol("GOOGL")).thenReturn(stock2);
//
//        Stock stock3 = new Stock();
//        stock3.setPrice(310.11);
//        when(stockRepository.findBySymbol("MSFT")).thenReturn(stock3);
//
//        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
//        when(orderRepository.findAll(orderFilterRequest.getPredicate())).thenReturn(List.of(order1, order2, order3));
//
//        var result1 = orderService.getAllOrders(orderFilterRequest);
//        var result2 = orderService.getOrdersByUser(orderFilterRequest);
//
//        Assertions.assertEquals(orders, result1);
//        Assertions.assertEquals(orders, result2);
    }
}
