package org.banka1.exchangeservice.controllers;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.option.BetDto;
import org.banka1.exchangeservice.domains.dtos.order.OrderFilterRequest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.entities.Order;
import org.banka1.exchangeservice.domains.entities.OrderAction;
import org.banka1.exchangeservice.domains.entities.OrderType;
import org.banka1.exchangeservice.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest extends IntegrationTest {

    @MockBean
    private OrderService orderService;

    @BeforeEach
    public void initMocks() {
        when(orderService.getAllOrders(any())).thenReturn(Collections.emptyList());
        when(orderService.getOrdersByUser(any())).thenReturn(Collections.emptyList());
        when(orderService.makeOrder(any(), any())).thenReturn(new Order());
        doNothing().when(orderService).approveOrder(any(), any());
        doNothing().when(orderService).rejectOrder(any(), any());
        doNothing().when(orderService).placeBet(any(), any(), any());
        doNothing().when(orderService).rejectBet(any(), any());
        when(orderService.getMyBets(any())).thenReturn(Collections.emptyList());
        when(orderService.getAllOptions()).thenReturn(Collections.emptyList());
        doNothing().when(orderService).finishOptionBet(any(), any());
    }


    @Test
    public void makeOrderSuccessfully() throws Exception {
        OrderRequest orderRequest = new OrderRequest();

        mockMvc.perform(post("/api/orders/make-order")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getOrders() throws Exception{
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();

        mockMvc.perform(post("/api/orders/all")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderFilterRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getOrdersByUser() throws Exception{
        OrderFilterRequest orderFilterRequest = new OrderFilterRequest();

        mockMvc.perform(post("/api/orders/by-user")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(orderFilterRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void approveOrder() throws Exception {
        mockMvc.perform(post("/api/orders/approve/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void rejectOrder() throws Exception{
        mockMvc.perform(post("/api/orders/reject/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void placeBet() throws Exception{
        BetDto betDto = new BetDto();

        mockMvc.perform(post("/api/orders/option/bet/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(betDto)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void rejectBet() throws Exception{
        mockMvc.perform(delete("/api/orders/option/reject/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getMyBets() throws Exception{
        mockMvc.perform(get("/api/orders/options/myBets")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getAllOptions() throws Exception{
        mockMvc.perform(get("/api/orders/options")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void finishOptionBet() throws Exception {
        mockMvc.perform(post("/api/orders/options/finish-bet/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
