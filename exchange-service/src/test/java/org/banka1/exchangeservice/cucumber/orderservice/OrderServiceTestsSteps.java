package org.banka1.exchangeservice.cucumber.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;
import org.banka1.exchangeservice.domains.dtos.user.UserListingCreateDto;
import org.banka1.exchangeservice.domains.dtos.user.UserListingDto;
import org.banka1.exchangeservice.domains.entities.ListingType;
import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderAction;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.entities.OrderType;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class OrderServiceTestsSteps extends OrderServiceTestsConfig{

    @Autowired
    private OrderService orderService;
    @Autowired
    private ForexService forexService;
    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ForexRepository forexRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${user.service.endpoint}")
    private String userServiceUrl;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private String token;
    private Order order;

    @When("User logged in")
    public void userLoggedIn() {
        List<String> role = new ArrayList<>();
        role.add("ROLE_ADMIN");

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("roles", role);

        token = Jwts.builder()
                .setClaims(claims)
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();
    }

    @And("Order is made")
    public void orderIsMade() {
//        forexRepository.deleteAll();
//        stockRepository.deleteAll();
//        orderRepository.deleteAll();
//
//        try {
//            forexService.loadForex();
//            stockService.loadStocks();
//        } catch (Exception e) {
//            fail(e.getMessage());
//        }
//
//        OrderRequest orderRequest = new OrderRequest("AAPL", ListingType.STOCK, 1, OrderAction.BUY, OrderType.MARKET_ORDER,
//                100D, 100D, false, false);
//
//        order = orderService.makeOrder(orderRequest, "Bearer " + token);
//
//        assertNotNull(order);
//        assertEquals(order.getUserId(), 1L);
//        assertEquals(order.getOrderStatus(), OrderStatus.APPROVED);
    }

    @Then("Get order")
    public void getOrder() {

        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        orderFilterRequest.setOrderStatus(OrderStatus.ON_HOLD);
        orderFilterRequest.setUserId(1L);
        orderFilterRequest.setDone(true);

        var orders = orderService.getOrdersByUser(orderFilterRequest);

        assertNotNull(orders);
    }

    @And("User listing is made")
    public void userListingIsMade(){
//        Order order = Order.builder().userId(1L).listingSymbol("AMZN").quantity(50).listingType(ListingType.STOCK).build();
//
//        UserListingCreateDto userListingCreateDto = new UserListingCreateDto();
//        userListingCreateDto.setSymbol(order.getListingSymbol());
//        userListingCreateDto.setQuantity(order.getQuantity());
//        userListingCreateDto.setListingType(order.getListingType());
//
//        try {
//            String body = objectMapper.writeValueAsString(userListingCreateDto);
//            String url = userServiceUrl + "/user-listings/create?userId=" + order.getUserId();
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(url))
//                    .header("Authorization", "Bearer " + token)
//                    .header("Content-Type", "application/json")
//                    .method("POST", HttpRequest.BodyPublishers.ofString(body))
//                    .build();
//
//            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
//
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Then("Get user listing")
    public void getUserListing(){
        UserListingDto userListingDto = new UserListingDto();
        userListingDto.setListingType(ListingType.STOCK);
        userListingDto.setSymbol("AMZN");
        userListingDto.setQuantity(50);

//        UserListingDto resultUserListingDto = orderService.getUserListing(1L, ListingType.STOCK, "AMZN", "Bearer " + token);
//
//        Assertions.assertNotNull(resultUserListingDto);
//        Assertions.assertEquals(userListingDto.getListingType(), resultUserListingDto.getListingType());
//        Assertions.assertEquals(userListingDto.getSymbol(), resultUserListingDto.getSymbol());
//        Assertions.assertEquals(userListingDto.getQuantity(), resultUserListingDto.getQuantity());
    }

    @Then("Approve and get order")
    public void approveAndGetOrder() {
//        order.setOrderStatus(OrderStatus.REJECTED);
//        orderRepository.save(order);
//
//        orderService.approveOrder("Bearer " + token, order.getId());
//
//        Optional<Order> optionalOrder = orderRepository.findById(order.getId());
//        Order orderFromDatabase = optionalOrder.stream().findFirst().orElse(null);
//
//        Assertions.assertNotNull(orderFromDatabase);
//        Assertions.assertEquals(orderFromDatabase.getOrderStatus(), OrderStatus.APPROVED);
    }

    @Then("Reject and get order")
    public void rejectAndGetOrder() {
//        orderService.rejectOrder("Bearer " + token, order.getId());
//
//        Optional<Order> optionalOrder = orderRepository.findById(order.getId());
//        Order orderFromDatabase = optionalOrder.stream().findFirst().orElse(null);
//
//        Assertions.assertNotNull(orderFromDatabase);
//        Assertions.assertEquals(orderFromDatabase.getOrderStatus(), OrderStatus.REJECTED);
    }

    @And("An invalid rejected order search is made")
    public void anInvalidRejectedOrderSearchIsMade() {
//        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.rejectOrder("Bearer " + token, 999L));
    }

    @And("An invalid approved order search is made")
    public void anInvalidApprovedOrderSearchIsMade() {
//        Assertions.assertThrows(NotFoundExceptions.class, () -> orderService.approveOrder("Bearer " + token, 999L));
    }

    @Then("Get user information from user service")
    public void getUserInformationFromUserService() {
//        var result = orderService.getUserDtoFromUserService("Bearer " + token);
//
//        UserDto userDto = new UserDto();
//        userDto.setId(1L);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(userDto.getId(), result.getId());
    }

    @Then("User daily limit can be reduced")
    public void userDailyLimitIsUpdated() {
//        double limitDecrease = 10000;
//        var userDtoBeforeLimitDecrease = orderService.getUserDtoFromUserService("Bearer " + token);
//
//        orderService.reduceDailyLimitForUser("Bearer " + token, 1L, limitDecrease);
//        var userDtoAfterLimitDecrease = orderService.getUserDtoFromUserService("Bearer " + token);
//
//        Assertions.assertEquals(userDtoBeforeLimitDecrease.getDailyLimit() - limitDecrease,
//                userDtoAfterLimitDecrease.getDailyLimit());
    }

    @Then("User bank account balance can be updated")
    public void userBankAccountBalanceIsUpdated() {
//        double accountBalanceToUpdate = 10.00;
//        var userDtoBeforeUpdate = orderService.getUserDtoFromUserService("Bearer " + token);
//
//        String url = userServiceUrl + "/users/increase-balance?increaseAccount=" + accountBalanceToUpdate;
//        orderService.updateBankAccountBalance("Bearer " + token, url);
//        var userDtoAfterUpdate = orderService.getUserDtoFromUserService("Bearer " + token);
//
//        Assertions.assertEquals(userDtoBeforeUpdate.getBankAccount().getAccountBalance() + accountBalanceToUpdate,
//                userDtoAfterUpdate.getBankAccount().getAccountBalance());
    }
}
