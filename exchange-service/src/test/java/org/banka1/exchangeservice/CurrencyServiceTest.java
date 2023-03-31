package org.banka1.exchangeservice;

import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.banka1.exchangeservice.services.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class CurrencyServiceTest {

    private CurrencyRepository currencyRepository;
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        this.currencyRepository = mock(CurrencyRepository.class);
        this.currencyService = new CurrencyService(currencyRepository);
    }

    @Test
    public void getCurrenciesSuccessfully () throws IOException {

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
}
