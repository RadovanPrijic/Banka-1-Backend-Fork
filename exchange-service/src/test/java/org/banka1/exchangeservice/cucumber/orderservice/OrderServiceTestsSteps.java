package org.banka1.exchangeservice.cucumber.orderservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.entities.ListingType;
import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderAction;
import org.banka1.exchangeservice.domains.entities.OrderStatus;
import org.banka1.exchangeservice.domains.entities.OrderType;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.OrderService;
import org.banka1.exchangeservice.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @When("Make order")
    public void make_order() {
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

         String token = Jwts.builder()
                .setClaims(claims)
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY).compact();

        OrderRequest orderRequest = new OrderRequest("AAPL", ListingType.STOCK, 1, OrderAction.BUY, OrderType.MARKET_ORDER,
                100D, 100D, false, false);

         Order order = orderService.makeOrder(orderRequest, token);

        assertNotNull(order);
        assertEquals(order.getUserId(), 1L);
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
}
