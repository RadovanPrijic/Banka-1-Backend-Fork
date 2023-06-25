package org.banka1.exchangeservice.controllers;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.order.OrderRequest;
import org.banka1.exchangeservice.domains.dtos.stock.StockResponseDtoFlask;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.services.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class StockControllerTest extends IntegrationTest {

    @MockBean
    private StockService stockService;

    @BeforeEach
    public void initMocks() {
        when(stockService.getStocks(any(), any(), any())).thenReturn(Page.empty());
        when(stockService.getStockTimeSeries(any(), any())).thenReturn(new StockResponseDtoFlask());
        when(stockService.getStockById(any())).thenReturn(Optional.empty());
        when(stockService.getStockSymbols()).thenReturn(Collections.emptyList());
        when(stockService.getStockBySymbol(any())).thenReturn(new Stock());
    }

    @Test
    void getAllStocks() throws Exception {
        mockMvc.perform(get("/api/stocks")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getStockTimeSeries() throws Exception {
        mockMvc.perform(get("/api/stocks/time-series?timeSeries=DAILY&symbol=AAPL")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getStock() throws Exception {
        mockMvc.perform(get("/api/stocks/1")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getStockSymbols() throws Exception {
        mockMvc.perform(get("/api/stocks/symbols")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getStockBySymbol() throws Exception {
        mockMvc.perform(get("/api/stocks/symbols/AAPL")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
}