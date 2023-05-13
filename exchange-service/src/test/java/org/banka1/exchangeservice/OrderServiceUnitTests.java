package org.banka1.exchangeservice;

import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.entities.*;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderServiceUnitTests {

    private OrderRepository orderRepository;
    private ForexRepository forexRepository;
    private StockRepository stockRepository;
    private ForexService forexService;
    private StockService stockService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderRepository = mock(OrderRepository.class);
        this.forexRepository = mock(ForexRepository.class);
        this.stockRepository = mock(StockRepository.class);
        this.forexService = mock(ForexService.class);
        this.stockService = mock(StockService.class);
        this.orderService = new OrderService(orderRepository, forexRepository, stockRepository, forexService, stockService);
    }

    @Test
    public void calculateThePriceForForexTest(){
        ListingType listingType = ListingType.FOREX;
        String symbol = "USD/CAD";
        Integer qty = 5;

        Forex forex = new Forex();
        forex.setExchangeRate(1.35);

        when(forexRepository.findBySymbol("USD/CAD")).thenReturn(forex);
        var result = orderService.calculateThePrice(listingType, symbol, qty);

        Assertions.assertEquals(forex.getExchangeRate() * qty, result);
    }

    @Test
    public void calculateThePriceForStockTest(){
        ListingType listingType = ListingType.STOCK;
        String symbol = "AAPL";
        Integer qty = 5;

        Stock stock = new Stock();
        stock.setPrice(173.15);

        when(stockRepository.findBySymbol("AAPL")).thenReturn(stock);
        var result = orderService.calculateThePrice(listingType, symbol, qty);

        Assertions.assertEquals(stock.getPrice() * qty, result);
    }

    @Test
    public void getOrdersTest(){
        Order order1 = Order.builder().listingType(ListingType.STOCK).listingSymbol("AAPL").quantity(5).build();
        Order order2 = Order.builder().listingType(ListingType.STOCK).listingSymbol("GOOGL").quantity(10).build();
        Order order3 = Order.builder().listingType(ListingType.STOCK).listingSymbol("MSFT").quantity(15).build();
        ArrayList<Order> orders = new ArrayList<>();
        orders.add(order1);
        orders.add(order2);
        orders.add(order3);

        Stock stock1 = new Stock();
        stock1.setPrice(173.15);
        when(stockRepository.findBySymbol("AAPL")).thenReturn(stock1);

        Stock stock2 = new Stock();
        stock2.setPrice(116.57);
        when(stockRepository.findBySymbol("GOOGL")).thenReturn(stock2);

        Stock stock3 = new Stock();
        stock3.setPrice(310.11);
        when(stockRepository.findBySymbol("MSFT")).thenReturn(stock3);

        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        when(orderRepository.findAll(orderFilterRequest.getPredicate())).thenReturn(List.of(order1, order2, order3));

        var result1 = orderService.getAllOrders(orderFilterRequest);
        var result2 = orderService.getOrdersByUser(orderFilterRequest);

        Assertions.assertEquals(orders, result1);
        Assertions.assertEquals(orders, result2);
    }
}
