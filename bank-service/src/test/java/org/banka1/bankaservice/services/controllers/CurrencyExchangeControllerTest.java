package org.banka1.bankaservice.services.controllers;

import org.banka1.bankservice.controllers.CurrencyExchangeController;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferConfirmDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferCreateDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ConversionTransferDto;
import org.banka1.bankservice.domains.dtos.currency_exchange.ExchangePairDto;
import org.banka1.bankservice.services.CurrencyExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = CurrencyExchangeController.class)
@AutoConfigureMockMvc
public class CurrencyExchangeControllerTest {

    @MockBean
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void initMocks() {
        when(currencyExchangeService.findConversionTransferById(any())).thenReturn(new ConversionTransferDto());
        when(currencyExchangeService.findAllConversionTransfersForLoggedInUser()).thenReturn(List.of(new ConversionTransferDto()));
        when(currencyExchangeService.findAllConversionTransfersForAccount("123456")).thenReturn(List.of(new ConversionTransferDto()));
        when(currencyExchangeService.findAllExchangePairs()).thenReturn(List.of(new ExchangePairDto()));
        when(currencyExchangeService.convertMoney(any(ConversionTransferCreateDto.class))).thenReturn(new ConversionTransferConfirmDto());
        when(currencyExchangeService.confirmConversionTransfer(any(ConversionTransferConfirmDto.class), anyBoolean())).thenReturn(new ConversionTransferDto());
    }

    @Test
    public void testGetConversionTransferById() throws Exception {
        Long id = 1L;

        mockMvc.perform(get("/api/bank/conversion/{id}", id))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetAllConversionTransfersForLoggedInUser() throws Exception {

        mockMvc.perform(get("/api/bank/user_conversions"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetAllConversionTransfersForAccount() throws Exception {
        String accountNumber = "123456";

        mockMvc.perform(get("/api/bank/account_conversions")
                        .param("accountNumber", accountNumber))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testGetAllExchangePairs() throws Exception {

        mockMvc.perform(get("/api/bank/exchange_pairs"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    public void testConvertMoney() throws Exception {
        ConversionTransferCreateDto conversionTransferCreateDto = new ConversionTransferCreateDto();

        mockMvc.perform(post("/api/bank/convert_money")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void testConfirmConversionTransfer() throws Exception {
        ConversionTransferConfirmDto conversionTransferConfirmDto = new ConversionTransferConfirmDto();

        mockMvc.perform(post("/api/bank/confirm_conversion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}
