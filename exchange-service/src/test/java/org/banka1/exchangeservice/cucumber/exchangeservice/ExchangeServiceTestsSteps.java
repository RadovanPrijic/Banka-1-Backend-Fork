package org.banka1.exchangeservice.cucumber.exchangeservice;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.dtos.exchange.ExchangeCSV;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.services.ExchangeService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ExchangeServiceTestsSteps extends ExchangeServiceTestsConfig{

    @Autowired
    private ExchangeService exchangeService;


    @When("Stavlja se lista berze")
    public void stavlja_se_lista_berze(){
        var exchange1 = new ExchangeCSV("Jakarta Futures Exchange (bursa Berjangka Jakarta)","BBJ","XBBJ","Indonesia","Indonesian Rupiah","Asia/Jakarta","09:00","17:30");
        var exchange2 = new ExchangeCSV("Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne","10:00","16:00");
        var exchange3 = new ExchangeCSV("Cboe Edga U.s. Equities Exchange Dark","EDGADARK","EDGD","United States","United States Dollar","America/New_York","09:30", "16:00");
        List<ExchangeCSV> csvBeanList = List.of(exchange1, exchange2, exchange3);

        try {
            exchangeService.persistExchanges(csvBeanList);

        }catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Then("Berza je sacuvana u bazi podataka")
    public void  berza_je_sacuvana_u_bazi_podataka(){
        try {
            Exchange exchange1 = exchangeService.findByAcronym("BBJ");
            assertNotNull(exchange1);
            assertEquals("BBJ", exchange1.getExcAcronym());

            Exchange exchange2 = exchangeService.findByAcronym("SFE");
            assertNotNull(exchange2);
            assertEquals("SFE", exchange2.getExcAcronym());

            Exchange exchange3 = exchangeService.findByAcronym("EDGADARK");
            assertNotNull(exchange3);
            assertEquals("EDGADARK", exchange3.getExcAcronym());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
