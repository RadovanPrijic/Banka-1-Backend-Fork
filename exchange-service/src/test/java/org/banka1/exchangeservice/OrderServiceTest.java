package org.banka1.exchangeservice;

import org.banka1.exchangeservice.domains.dtos.user.UserDto;

import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
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

import java.util.Optional;

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
        this.orderService = new OrderService(orderRepository, forexRepository, stockRepository, forexService, stockService);
    }

    @Test
    public void getUserDtoFromUserServiceTest(){
        var result = orderService.getUserDtoFromUserService(getToken());

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
    }

    @Test
    public void rejectOrderTest(){
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.rejectOrder(getToken(), 1L);

        verify(orderRepository, times(1)).save(order);
        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.REJECTED);
    }

    @Test
    public void rejectOrderNotFoundExceptionTest(){
        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);

        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.rejectOrder(getToken(), 1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void approveOrderTest(){
        Order order = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.approveOrder(getToken(), 1L);

        verify(orderRepository, times(1)).save(order);
        Assertions.assertEquals(order.getOrderStatus(), OrderStatus.APPROVED);
    }

    @Test
    public void approveOrderNotFoundExceptionTest(){
        when(orderRepository.findById(1L)).thenThrow(NotFoundExceptions.class);

        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.approveOrder(getToken(), 1L));
        verify(orderRepository, times(1)).findById(1L);
        verifyNoMoreInteractions(orderRepository);
    }

    @Test
    public void reduceDailyLimitForUserTest(){
        double limitDecrease = 10000;
        var userDtoBeforeLimitDecrease = orderService.getUserDtoFromUserService(getToken());

        orderService.reduceDailyLimitForUser(getToken(), 1L, limitDecrease);
        var userDtoAfterLimitDecrease = orderService.getUserDtoFromUserService(getToken());

        Assertions.assertEquals(userDtoBeforeLimitDecrease.getBankAccount().getDailyLimit() - limitDecrease,
                userDtoAfterLimitDecrease.getBankAccount().getDailyLimit());
    }

    @Test
    public void  updateBankAccountBalanceTest() {
        double accountBalanceToUpdate = 10.00;
        var userDtoBeforeUpdate = orderService.getUserDtoFromUserService(getToken());

        String url = userServiceUrl + "/users/increase-balance?increaseAccount=" + accountBalanceToUpdate;
        orderService.updateBankAccountBalance(getToken(), url);
        var userDtoAfterUpdate = orderService.getUserDtoFromUserService(getToken());

        Assertions.assertEquals(userDtoBeforeUpdate.getBankAccount().getAccountBalance() + accountBalanceToUpdate,
                userDtoAfterUpdate.getBankAccount().getAccountBalance());
    }

    //TODO Promeniti nazad u userServiceUrl varijablu ono gde sam prilepio localhost adresu u OrderService (na TRI mesta)!

//    public UserListingDto getUserListing(Long userId, ListingType listingType, String symbol, String token) {
//        String url = "http://localhost:8080/api" + "/user-listings?userId=" + userId;
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(url))
//                .header("Authorization", "Bearer " + token)
//                .method("GET", HttpRequest.BodyPublishers.noBody())
//                .build();
//
//        UserListingDto userListingDto = null;
//        try {
//            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//            UserListingDto[] userListings = objectMapper.readValue(response.body(), UserListingDto[].class);
//
//            userListingDto = Stream.of(userListings)
//                    .filter(ul -> ul.getListingType() == listingType && ul.getSymbol().equals(symbol))
//                    .findFirst()
//                    .orElse(null);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return userListingDto;
//    }
}
