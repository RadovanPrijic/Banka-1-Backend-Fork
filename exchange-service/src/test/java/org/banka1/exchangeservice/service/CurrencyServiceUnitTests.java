package org.banka1.exchangeservice.service;

import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.entities.Currency;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.banka1.exchangeservice.services.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CurrencyServiceUnitTests {

    private CurrencyRepository currencyRepository;
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        this.currencyRepository = mock(CurrencyRepository.class);
        this.currencyService = new CurrencyService(currencyRepository);
    }

    @Test
    public void persistCurrenciesSuccessfully (){
        CurrencyCsvBean currencyCsvBean1 = new CurrencyCsvBean("EUR", "Euro");
        CurrencyCsvBean currencyCsvBean2 = new CurrencyCsvBean("GBP", "British Pound Sterling");
        CurrencyCsvBean currencyCsvBean3 = new CurrencyCsvBean("USD", "United States Dollar");
        List<CurrencyCsvBean> csvBeanList = List.of(currencyCsvBean1, currencyCsvBean2, currencyCsvBean3);

        currencyService.persistCurrencies(csvBeanList);

        verify(currencyRepository, times(1)).saveAll((Mockito.anyCollection()));
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void findCurrencyByCurrencyNameSuccessfully(){
        var currency = new Currency();
        currency.setId(1L);
        currency.setCurrencyName("Euro");
        currency.setCurrencyCode("EUR");
        currency.setCurrencySymbol("€");

        when(currencyRepository.findByCurrencyName(anyString())).thenReturn(Optional.of(currency));
        var result = currencyService.findCurrencyByCurrencyName("Euro");

        assertEquals("Euro", result.getCurrencyName());
        assertEquals("EUR", result.getCurrencyCode());

        verify(currencyRepository, times(1)).findByCurrencyName(anyString());
        verifyNoMoreInteractions(currencyRepository);
    }

    @Test
    void findCurrencyByCurrencyNameThrowsNotFoundException(){
        when(currencyRepository.findByCurrencyName(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundExceptions.class, () -> currencyService.findCurrencyByCurrencyName("Euro"));

        verify(currencyRepository, times(1)).findByCurrencyName(anyString());
        verifyNoMoreInteractions(currencyRepository);
    }
}
