package org.banka1.exchangeservice.controllers;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.option.OptionFilterRequest;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.banka1.exchangeservice.services.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
