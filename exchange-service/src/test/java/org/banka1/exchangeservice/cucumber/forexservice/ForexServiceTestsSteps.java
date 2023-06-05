package org.banka1.exchangeservice.cucumber.forexservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.banka1.exchangeservice.domains.dtos.forex.ForexFilterRequest;
import org.banka1.exchangeservice.domains.dtos.forex.TimeSeriesForexEnum;
import org.banka1.exchangeservice.services.ForexService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class ForexServiceTestsSteps extends ForexServiceTestsConfig{

    @Autowired
    private ForexService forexService;

    @When("load forexes")
    public void load_forexes() {
        try {
            forexService.loadForex();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Then("returning forexes")
    public void returning_forexes() {
//        ForexFilterRequest forexFilterRequest = new ForexFilterRequest();
//        forexFilterRequest.setFromCurrencyCode("EUR");
//        forexFilterRequest.setToCurrencyCode("USD");
//        var forexList = forexService.getForexes(0, 10, forexFilterRequest);
//
//        assertNotNull(forexList);
    }

    @Then("returning forexes with time series")
    public void returning_forexes_with_time_series() {

        var forexTimeSeries = forexService.getForexByTimeSeries("EUR", "USD", TimeSeriesForexEnum.MONTHLY);

        assertNotNull(forexTimeSeries);
        assertEquals(forexTimeSeries.getFromCurrency(), "EUR");
        assertEquals(forexTimeSeries.getToCurrency(), "USD");
    }
}
