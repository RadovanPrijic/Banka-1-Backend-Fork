package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.user.Position;
import org.banka1.exchangeservice.domains.dtos.user.UserDto;
import org.banka1.exchangeservice.domains.dtos.user.UserListingCreateDto;
import org.banka1.exchangeservice.domains.dtos.user.UserListingDto;
import org.banka1.exchangeservice.domains.entities.*;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.domains.mappers.OrderMapper;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.banka1.exchangeservice.repositories.OrderRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ForexRepository forexRepository;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ForexService forexService;
    private final StockService stockService;

    @Value("${user.service.endpoint}")
    private String userServiceUrl;


    public OrderService(OrderRepository orderRepository, ForexRepository forexRepository,
                        StockRepository stockRepository, ForexService forexService,
                        StockService stockService) {
        this.orderRepository = orderRepository;
        this.forexRepository = forexRepository;
        this.stockRepository = stockRepository;
        this.forexService = forexService;
        this.stockService = stockService;
    }

    public Order makeOrder(OrderRequest orderRequest, String token) {
        UserDto userDto = getUserDtoFromUserService(token);
        Double expectedPrice = calculateThePrice(orderRequest.getListingType(),orderRequest.getSymbol(),orderRequest.getQuantity());

        Order order = new Order();
        order.setEmail(userDto.getEmail());
        order.setUserId(userDto.getId());
        order.setExpectedPrice(expectedPrice);
        order.setRemainingQuantity(orderRequest.getQuantity());
        order.setLastModified(new Date());

        if(userDto.getBankAccount().getAccountBalance() < expectedPrice) order.setOrderStatus(OrderStatus.REJECTED);
        else if(userDto.getBankAccount().getDailyLimit() - expectedPrice < 0) order.setOrderStatus(OrderStatus.ON_HOLD);
        else order.setOrderStatus(OrderStatus.APPROVED);

        reduceDailyLimitForUser(token, userDto.getId(), expectedPrice);
        OrderMapper.INSTANCE.updateOrderFromOrderRequest(order, orderRequest);
        orderRepository.save(order);

        if(order.getOrderStatus().equals(OrderStatus.APPROVED))
            mockExecutionOfOrder(order, token);

        return order;
    }

    private Double calculateThePrice(ListingType listingType, String symbol, Integer quantity){
        if(listingType.equals(ListingType.FOREX)){
         Forex forex = forexRepository.findBySymbol(symbol);
         return forex.getExchangeRate() * quantity;
        } else if (listingType.equals(ListingType.STOCK)) {
            Stock stock = stockRepository.findBySymbol(symbol);
            return stock.getPrice() * quantity;
        }
        return 0.0;
    }

    private UserDto getUserDtoFromUserService(String token){
        String url = userServiceUrl + "/users/my-profile";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        UserDto userDto = null;
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            userDto = objectMapper.readValue(response.body(), UserDto.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        return userDto;
    }

    private void reduceDailyLimitForUser(String token,Long userId, Double decreaseLimit){
        String url = userServiceUrl + "/users/reduce-daily-limit?userId=" + userId + "&decreaseLimit=" + decreaseLimit;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .method("PUT", HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    @Async
    public void mockExecutionOfOrder(Order order, String token) {
        Runnable runnable = () -> {
            UserListingDto userListingDto = getUserListing(order.getUserId(), order.getListingType(), order.getListingSymbol(), token);
            if(userListingDto == null) {
                UserListingCreateDto userListingCreateDto = new UserListingCreateDto();
                userListingCreateDto.setSymbol(order.getListingSymbol());
                userListingCreateDto.setQuantity(0);
                userListingCreateDto.setListingType(order.getListingType());

                try {
                    String body = objectMapper.writeValueAsString(userListingCreateDto);
                    String url = userServiceUrl + "/user-listings/create?userId=" + order.getUserId();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .header("Authorization", token)
                            .header("Content-Type", "application/json")
                            .method("POST", HttpRequest.BodyPublishers.ofString(body))
                            .build();

                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    String jsonUserListing = response.body();
                    userListingDto = objectMapper.readValue(jsonUserListing, UserListingDto.class);

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            if(order.getOrderAction() == OrderAction.SELL && order.getQuantity() > userListingDto.getQuantity()) {
                order.setOrderStatus(OrderStatus.REJECTED);
                return;
            }

            Long listingId = userListingDto.getId();
            while(!order.getDone()){
                try {
                    Thread.sleep(10000);
                } catch (Exception e){
                    e.printStackTrace();
                }

                Random random = new Random();
                int quantity = random.nextInt(order.getRemainingQuantity() + 1);
                if (order.isAllOrNone() && quantity != order.getQuantity()) continue;

                double askPrice = 0;
                double bidPrice = 0;
                if (order.getListingType() == ListingType.FOREX) {
                    Forex forex = forexRepository.findBySymbol(order.getListingSymbol());
                    forexService.updateForexes(Collections.singletonList(forex));

                    askPrice = forex.getAskPrice();
                    bidPrice = forex.getBidPrice();
                } else if (order.getListingType() == ListingType.STOCK) {
                    Stock stock = stockRepository.findBySymbol(order.getListingSymbol());
                    stockService.updateStocks(Collections.singletonList(stock));

                    askPrice = stock.getPrice();
                    bidPrice = stock.getPrice();
                }

                switch (order.getOrderType()) {
                    case STOP_ORDER -> {
                        if ((order.getOrderAction() == OrderAction.BUY && order.getStopValue() < askPrice)
                                || (order.getOrderAction() == OrderAction.SELL && order.getStopValue() > bidPrice)) {
                            order.setOrderType(OrderType.MARKET_ORDER);
                        }
                        else {
                            continue;
                        }
                    }
                    case LIMIT_ORDER -> {
                        if ((order.getOrderAction() == OrderAction.BUY && askPrice > order.getLimitValue())
                                || (order.getOrderAction() == OrderAction.SELL && bidPrice < order.getLimitValue())) {
                            continue;
                        }
                    }
                    case STOP_LIMIT_ORDER -> {
                        if ((order.getOrderAction() == OrderAction.BUY && order.getStopValue() > askPrice)
                                || (order.getOrderAction() == OrderAction.SELL && order.getStopValue() < bidPrice)
                                || ((order.getOrderAction() == OrderAction.BUY && askPrice > order.getLimitValue())
                                || (order.getOrderAction() == OrderAction.SELL && bidPrice < order.getLimitValue()))) {
                            continue;
                        }
                    }
                }

                int newQuantity;
                if(order.getOrderAction() == OrderAction.BUY) {
                    newQuantity = quantity + userListingDto.getQuantity();
                } else {
                    newQuantity = userListingDto.getQuantity() - quantity;
                }

                order.setRemainingQuantity(order.getRemainingQuantity() - quantity);
                if(order.getRemainingQuantity() == 0) order.setDone(true);

                Double accountBalanceToUpdate = calculateThePrice(order.getListingType(), order.getListingSymbol(), quantity);
                String urlBankAccount;
                if(order.getOrderAction() == OrderAction.BUY) urlBankAccount = userServiceUrl + "/users/decrease-balance?decreaseAccount=" + accountBalanceToUpdate;
                else urlBankAccount = userServiceUrl + "/users/increase-balance?increaseAccount=" + accountBalanceToUpdate;

                updateBankAccountBalance(token, urlBankAccount);
                System.out.println("UPDATED BALANCE ACCOUNT");


                String url = userServiceUrl + "/user-listings/update/" + listingId + "?newQuantity=" + newQuantity;
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", token)
                        .header("Content-Type", "application/json")
                        .method("PUT", HttpRequest.BodyPublishers.ofString("")) // mozda treba mozda ne treba
                        .build();
                try {
                    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                    userListingDto = objectMapper.readValue(response.body(), UserListingDto.class);
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                order.setLastModified(new Date());
                orderRepository.save(order);
            }

        };

        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void updateBankAccountBalance(String token, String url){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .header("Content-Type", "application/json")
                .method("PUT", HttpRequest.BodyPublishers.ofString(""))
                .build();
        try {
            HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void rejectOrder(String token, Long orderId) {
        UserDto userDto = getUserDtoFromUserService(token);
        if(userDto.getPosition() == Position.ADMINISTRATOR){
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundExceptions("order not found"));
            order.setOrderStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
        }
    }

    public void approveOrder(String token, Long orderId) {
        UserDto userDto = getUserDtoFromUserService(token);
        if(userDto.getPosition() == Position.ADMINISTRATOR){
            Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundExceptions("order not found"));
            order.setOrderStatus(OrderStatus.APPROVED);
            orderRepository.save(order);
            mockExecutionOfOrder(order, token);
        }
    }

    public List<Order> getAllOrders(OrderFilterRequest orderFilterRequest) {
        Iterable<Order> orderIterable = orderRepository.findAll(orderFilterRequest.getPredicate());
        List<Order> orders = new ArrayList<>();
        orderIterable.forEach(orders::add);
        orders.forEach(order -> order.setExpectedPrice(calculateThePrice(order.getListingType(), order.getListingSymbol(), order.getQuantity())));

        return orders;
    }

    public List<Order> getOrdersByUser(OrderFilterRequest orderFilterRequest) {
        Iterable<Order> orderIterable = orderRepository.findAll(orderFilterRequest.getPredicate());
        List<Order> orders = new ArrayList<>();
        orderIterable.forEach(orders::add);
        orders.forEach(order -> order.setExpectedPrice(calculateThePrice(order.getListingType(), order.getListingSymbol(), order.getQuantity())));

        return orders;
    }

    private UserListingDto getUserListing(Long userId, ListingType listingType, String symbol, String token) {
        String url = userServiceUrl + "/user-listings?userId=" + userId;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", token)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        UserListingDto userListingDto = null;
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            UserListingDto[] userListings = objectMapper.readValue(response.body(), UserListingDto[].class);

            userListingDto = Stream.of(userListings)
                    .filter(ul -> ul.getListingType() == listingType && ul.getSymbol().equals(symbol))
                    .findFirst()
                    .orElse(null);
        }catch (Exception e){
            e.printStackTrace();
        }

        return userListingDto;
    }
}
