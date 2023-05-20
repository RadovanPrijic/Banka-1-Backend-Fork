package org.banka1.exchangeservice.cucumber.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.user.UserListingCreateDto;
import org.banka1.exchangeservice.domains.dtos.user.UserListingDto;
import org.banka1.exchangeservice.domains.entities.ListingType;
import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderAction;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.entities.OrderType;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

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
    protected ObjectMapper objectMapper;

    @Value("${user.service.endpoint}")
    private String userServiceUrl;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private String token;

    @When("Make order")
    public void make_order() {
        forexRepository.deleteAll();
        stockRepository.deleteAll();
        try {
            forexService.loadForex();
            stockService.loadStocks();
        } catch (Exception e) {
            fail(e.getMessage());
        }

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
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

        OrderRequest orderRequest = new OrderRequest("AAPL", ListingType.STOCK, 1, OrderAction.BUY, OrderType.MARKET_ORDER,
                100D, 100D, false, false);

//        Order order = orderService.makeOrder(orderRequest, token);
//
//        assertNotNull(order);
//        assertEquals(order.getUserId(), 1L);
//        assertEquals(order.getOrderStatus(), OrderStatus.APPROVED);
    }
    @Then("Get order")
    public void get_order() {

        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();
        orderFilterRequest.setOrderStatus(OrderStatus.ON_HOLD);
        orderFilterRequest.setUserId(1L);
        orderFilterRequest.setDone(true);

        var orders = orderService.getOrdersByUser(orderFilterRequest);

        assertNotNull(orders);
    }

    @When("User listing is made")
    public void user_listing_is_made(){
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
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

        Order order = Order.builder().userId(1L).listingSymbol("AAPL").quantity(50).listingType(ListingType.STOCK).build();

        UserListingCreateDto userListingCreateDto = new UserListingCreateDto();
        userListingCreateDto.setSymbol(order.getListingSymbol());
        userListingCreateDto.setQuantity(order.getQuantity());
        userListingCreateDto.setListingType(order.getListingType());

        try {
            String body = objectMapper.writeValueAsString(userListingCreateDto);
            String url = userServiceUrl + "/user-listings/create?userId=" + order.getUserId();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(body))
                    .build();

//            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("Get user listing")
    public void get_user_listing(){
        UserListingDto userListingDto = new UserListingDto();
        userListingDto.setId(1L);
        userListingDto.setListingType(ListingType.STOCK);
        userListingDto.setSymbol("AAPL");
        userListingDto.setQuantity(50);

//        UserListingDto resultUserListingDto = orderService.getUserListing(1L, ListingType.STOCK, "AAPL", token);
//
//        Assertions.assertEquals(userListingDto, resultUserListingDto);
    }
}
