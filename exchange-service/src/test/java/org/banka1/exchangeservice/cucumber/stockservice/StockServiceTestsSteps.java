package org.banka1.exchangeservice.cucumber.stockservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.banka1.exchangeservice.domains.dtos.stock.TimeSeriesStockEnum;
import org.banka1.exchangeservice.services.StockService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class StockServiceTestsSteps extends StockServiceTestsConfig{

    @Autowired
    private StockService stockService;

    @When("load stocks")
    public void load_stocks() {
        try {
            stockService.loadStocks();
        } catch (IOException | InterruptedException e) {
            fail(e.getMessage());
        }
    }
    @Then("return stock with given id")
    public void return_stock_with_given_id() {
        var stock = stockService.getStockById(1L);

        assertEquals(1L, stock.get().getId());
    }
    @Then("returning all stocks")
    public void returning_all_stocks() {
        var stocks = stockService.getStocks(0, 2, "AAPL");

        assertNotNull(stocks);
    }

    @Then("returning all stocks with time series")
    public void returning_all_stocks_with_time_series() {
        var stocks = stockService.getStockTimeSeries("AAPL", TimeSeriesStockEnum.MONTHLY);

        assertEquals(stocks.getSymbol(), "AAPL");
    }
}
