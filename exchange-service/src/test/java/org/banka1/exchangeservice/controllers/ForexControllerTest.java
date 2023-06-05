package org.banka1.exchangeservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.forex.ForexFilterRequest;
import org.banka1.exchangeservice.domains.dtos.forex.ForexResponseTimeSeriesFlask;
import org.banka1.exchangeservice.domains.dtos.forex.TimeSeriesForexEnum;
import org.banka1.exchangeservice.services.ForexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ForexControllerTest extends IntegrationTest {

    @MockBean
    private ForexService forexService;

    @BeforeEach
    public void initMocks() {
        when(forexService.getForexes(any(), any(), any())).thenReturn(Page.empty());
        when(forexService.getForexByTimeSeries(any(), any(), any())).thenReturn(new ForexResponseTimeSeriesFlask());
    }

    @Test
    public void getForexes() throws Exception {
        ForexFilterRequest request = new ForexFilterRequest();

        mockMvc.perform(post("/api/forexes")
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getForexByTimeSeries() throws Exception{
        mockMvc.perform(get("/api/forexes?fromCurrency=USD&toCurrency=RSD&timeSeries=" + TimeSeriesForexEnum.DAILY)
                        .header("Authorization", "Bearer " + getToken())
                        .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }
}
