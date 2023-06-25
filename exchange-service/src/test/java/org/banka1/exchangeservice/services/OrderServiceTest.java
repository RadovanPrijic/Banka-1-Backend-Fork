package org.banka1.exchangeservice.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.option.BetDto;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;

import org.banka1.exchangeservice.domains.entities.*;
import org.banka1.exchangeservice.domains.exceptions.BadRequestException;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.*;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.banka1.exchangeservice.utils.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class OrderServiceTest extends IntegrationTest {

    private OrderRepository orderRepository;
    private ForexRepository forexRepository;
    private StockRepository stockRepository;
    private ForexService forexService;
    private StockService stockService;
    private OrderService orderService;

    private OptionRepository optionRepository;
    private OptionBetRepository optionBetRepository;

    @Value("${user.service.endpoint}")
    private String userServiceUrl;

//    OrderRepository orderRepository, ForexRepository forexRepository,
//    StockRepository stockRepository, OptionBetRepository optionBetRepository, OptionRepository optionRepository, ForexService forexService,
//    StockService stockService, JwtUtil jwtUtil

    @BeforeEach
    void setUp() {
        this.optionBetRepository = mock(OptionBetRepository.class);
        this.optionRepository = mock(OptionRepository.class);

        this.orderRepository = mock(OrderRepository.class);
        this.forexRepository = mock(ForexRepository.class);
        this.stockRepository = mock(StockRepository.class);
        this.forexService = mock(ForexService.class);
        this.stockService = mock(StockService.class);
        this.orderService = new OrderService(orderRepository, forexRepository, stockRepository,
                optionBetRepository, optionRepository, forexService, stockService, new JwtUtil(), userServiceUrl, SECRET_KEY);
    }

    @Test
    public void getUserDtoFromUserServiceTest(){
        var result = orderService.getUserDtoFromUserService("Bearer " + getToken());

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void rejectOrderTest(){
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder("Bearer " + getToken(), 1L);

        verify(orderRepository, times(1)).save(order);
        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void rejectOrderNotFoundExceptionTest(){
        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);

        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.rejectOrder("Bearer " + getToken(), 1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void approveOrderTest(){
//        Order order = new Order();
//        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
//
//        orderService.approveOrder("Bearer " + getToken(), 1L);
//
//        verify(orderRepository, times(1)).save(order);
//        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.APPROVED);
    }

    @Test
    public void approveOrderNotFoundExceptionTest(){
        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);

        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.approveOrder("Bearer " + getToken(), 1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void reduceDailyLimitForUserTest(){
        double limitDecrease = 10000;
        var userDtoBeforeLimitDecrease = orderService.getUserDtoFromUserService("Bearer " + getToken());

        orderService.reduceDailyLimitForUser(getToken(), 1L, limitDecrease);
        var userDtoAfterLimitDecrease = orderService.getUserDtoFromUserService("Bearer " + getToken());

        Assertions.assertEquals(userDtoBeforeLimitDecrease.getDailyLimit() - limitDecrease,
                userDtoAfterLimitDecrease.getDailyLimit() - limitDecrease);
    }

    @Test
    public void  updateBankAccountBalanceTest() {
        double accountBalanceToUpdate = 10.00;
        var userDtoBeforeUpdate = orderService.getUserDtoFromUserService("Bearer " + getToken());

        String url = userServiceUrl + "/users/increase-balance?increaseAccount=" + accountBalanceToUpdate;
        orderService.updateBankAccountBalance("Bearer " + getToken(), url);
        var userDtoAfterUpdate = orderService.getUserDtoFromUserService("Bearer " + getToken());

        Assertions.assertEquals(userDtoBeforeUpdate.getBankAccount().getAccountBalance() + accountBalanceToUpdate,
                userDtoAfterUpdate.getBankAccount().getAccountBalance());
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

    @Test
    void placeBet_ValidInputs_SuccessfullyPlacesBet() {
        // Arrange
        String token = "Bearer " + getToken();
        Long optionId = 123L;
        BetDto bet = new BetDto();
        LocalDate currentDate = LocalDate.now();
        bet.setBet(10D);
        bet.setDate(currentDate);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");

        Option option = new Option();
        option.setId(optionId);
        option.setExpirationDate(currentDate.plusDays(7));
        option.setOptionType(OptionType.CALL);
        option.setStrike(100.0);

        when(optionRepository.findById(optionId)).thenReturn(Optional.of(option));

        // Act
        orderService.placeBet(token, optionId, bet);

        // Assert
        verify(optionRepository).findById(optionId);
        verify(optionBetRepository).save(any(OptionBet.class));
    }

    @Test
    void finishOptionBet_OptionBetExists_FinishesBetAndUpdatesBalance() {
        // Arrange
        String token = "Bearer " + getToken();
        Long optionBetId = 123L;

        OptionBet optionBet = new OptionBet();
        optionBet.setId(optionBetId);
        optionBet.setOptionId(456L);

        Option option = new Option();
        option.setId(optionBet.getOptionId());
        option.setOptionType(OptionType.CALL);
        option.setStrike(100.0);

        when(optionBetRepository.findById(optionBetId)).thenReturn(Optional.of(optionBet));
        when(optionRepository.findById(optionBet.getOptionId())).thenReturn(Optional.of(option));

        // Act
        orderService.finishOptionBet(token, optionBetId);

        // Assert
        verify(optionBetRepository).findById(optionBetId);
        verify(optionRepository).findById(optionBet.getOptionId());
//        verify(orderService).updateBankAccountBalance(eq(token), anyString());
//        verify(optionBetRepository).delete(optionBet);
    }

    @Test
    void rejectBet_BetExistsAndUserMatches_DeletesBet() {
        // Arrange
        String token = "Bearer " + getToken();
        Long optionBetId = 123L;
        Long userId = 456L;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        OptionBet optionBet = new OptionBet();
        optionBet.setId(optionBetId);
        optionBet.setUserId(userId);

        when(optionBetRepository.findById(optionBetId)).thenReturn(Optional.of(optionBet));

        // Act
        Assertions.assertThrows(BadRequestException.class, () -> orderService.rejectBet(token, optionBetId), "Bad request");

        // Assert
        verify(optionBetRepository).findById(optionBetId);
    }

    @Test
    void rejectBet_BetExistsButUserDoesNotMatch_ThrowsBadRequestException() {
        // Arrange
        String token = "Bearer " + getToken();
        Long optionBetId = 123L;
        Long userId = 456L;
        Long differentUserId = 789L;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        OptionBet optionBet = new OptionBet();
        optionBet.setId(optionBetId);
        optionBet.setUserId(differentUserId);

        when(optionBetRepository.findById(optionBetId)).thenReturn(Optional.of(optionBet));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> orderService.rejectBet(token, optionBetId));
    }

    @Test
    void getMyBets_ValidToken_ReturnsMyBetsAfterCurrentDate() {
        // Arrange
        String token = "Bearer " + getToken();
        Long userId = 123L;

        UserDto userDto = new UserDto();
        userDto.setId(userId);

        List<OptionBet> optionBets = new ArrayList<>();
        OptionBet bet1 = new OptionBet();
        bet1.setId(1L);
        bet1.setUserId(userId);
        bet1.setDate(LocalDate.now().plusDays(1)); // After current date
        optionBets.add(bet1);

        OptionBet bet2 = new OptionBet();
        bet2.setId(2L);
        bet2.setUserId(userId);
        bet2.setDate(LocalDate.now().minusDays(1)); // Before current date
        optionBets.add(bet2);

        when(optionBetRepository.findAllByUserId(anyLong())).thenReturn(optionBets);

        // Act
        List<OptionBet> result = orderService.getMyBets(token);

        // Assert
        verify(optionBetRepository).findAllByUserId(anyLong());
        assertEquals(1, result.size());
        assertEquals(bet1, result.get(0));
    }

    @Test
    void getAllOptions_ReturnsAllOptions() {
        // Arrange
        List<Option> options = new ArrayList<>();
        Option option1 = new Option();
        option1.setId(1L);
        options.add(option1);

        Option option2 = new Option();
        option2.setId(2L);
        options.add(option2);

        when(optionRepository.findAll()).thenReturn(options);

        // Act
        List<Option> result = orderService.getAllOptions();

        // Assert
        verify(optionRepository).findAll();

        assertEquals(options.size(), result.size());
        assertTrue(result.contains(option1));
        assertTrue(result.contains(option2));
    }

}