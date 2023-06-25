package org.banka1.exchangeservice.controllers;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.option.BetDto;
import org.banka1.exchangeservice.domains.dtos.option.OptionFilterRequest;
import org.banka1.exchangeservice.domains.entities.OptionBet;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.OptionService;
import org.banka1.exchangeservice.services.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OptionControllerTest extends IntegrationTest {

    @Autowired
    private OptionService optionService;
    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    public void setUp() {
        initStocks();
        optionService.loadOptions();
    }

    @AfterEach()
    public void removeContent() {
        stockRepository.deleteAll();
    }

    @Test
    public void getOptionsSuccessfully() throws Exception {
        OptionFilterRequest optionFilterRequest = new OptionFilterRequest();
        optionFilterRequest.setSymbol("TSLA");

        mockMvc.perform(post("/api/options")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(optionFilterRequest)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void placeBet_ValidRequest_ReturnsForbidden() throws Exception {
        // Arrange
        String token = "mocked_token";
        Long optionId = 123L;
        BetDto betDto = new BetDto(); // Create a valid BetDto object here

        // Act
        ResultActions resultActions = mockMvc.perform(post("/option/bet/{optionId}", optionId)
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(betDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void rejectBet_ValidRequest_ReturnsForbidden() throws Exception {
        // Arrange
        String token = "mocked_token";
        Long optionBetId = 123L;

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/option/reject/{optionBetId}", optionBetId)
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void getMyBets_ValidRequest_ReturnsMyBets() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/options/myBets")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void getMyBets_ValidRequest_ReturnsAllOptions() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/options")
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void finishOptionBet_ValidRequest_ReturnsForbidden() throws Exception {
        // Arrange
        String token = "mocked_token";
        Long optionId = 123L;
        BetDto betDto = new BetDto(); // Create a valid BetDto object here

        // Act
        ResultActions resultActions = mockMvc.perform(post("/options/finish-bet/{optionBetId}", optionId)
                .header("Authorization", "Bearer " + getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(betDto)));

        // Assert
        resultActions.andExpect(status().isNotFound());
    }

    private void initStocks() {
        Stock stock1 = new Stock();
        stock1.setSymbol("AAPL");
        stock1.setPrice(100D);

        Stock stock2 = new Stock();
        stock2.setSymbol("AMZN");
        stock2.setPrice(150D);

        Stock stock3 = new Stock();
        stock3.setSymbol("TSLA");
        stock3.setPrice(200D);

        stockRepository.save(stock1);
        stockRepository.save(stock2);
        stockRepository.save(stock3);
    }
}