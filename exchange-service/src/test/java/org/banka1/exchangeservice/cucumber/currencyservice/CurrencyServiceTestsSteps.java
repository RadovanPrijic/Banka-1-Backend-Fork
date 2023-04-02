package org.banka1.exchangeservice.cucumber.currencyservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class CurrencyServiceTestsSteps extends CurrencyServiceTestsConfig{

    @Autowired
    private CurrencyService currencyService;

    @When("dodaju se nove valute")
    public void dodaju_se_nove_valute(){
        CurrencyCsvBean currencyCsvBean1 = new CurrencyCsvBean("EUR", "Euro");
        CurrencyCsvBean currencyCsvBean2 = new CurrencyCsvBean("GBP", "British Pound Sterling");
        CurrencyCsvBean currencyCsvBean3 = new CurrencyCsvBean("USD", "United States Dollar");
        List<CurrencyCsvBean> csvBeanList = List.of(currencyCsvBean1, currencyCsvBean2, currencyCsvBean3);
        try {
            currencyService.persistCurrencies(csvBeanList);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("valute se sacuvaju u bazi podataka")
    public void valute_se_sacuvaju_u_bazi_podataka(){
        try {
            CurrencyCsvBean currency1 = currencyService.findCurrencyByCurrencyName("Euro");
            assertNotNull(currency1);
            assertEquals("Euro", currency1.getCurrencyName());

            CurrencyCsvBean currency2 = currencyService.findCurrencyByCurrencyName("British Pound Sterling");
            assertNotNull(currency2);
            assertEquals("British Pound Sterling", currency2.getCurrencyName());

            CurrencyCsvBean currency3 = currencyService.findCurrencyByCurrencyName("United States Dollar");
            assertNotNull(currency3);
            assertEquals("United States Dollar", currency3.getCurrencyName());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
