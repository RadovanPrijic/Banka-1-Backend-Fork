package org.banka1.exchangeservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import org.banka1.exchangeservice.domains.dtos.exchange.ExchangeCSV;
import org.banka1.exchangeservice.services.ExchangeService;
import org.banka1.exchangeservice.services.ForexService;
import org.banka1.exchangeservice.services.StockService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import java.util.ArrayList;


@Component
@AllArgsConstructor
//@Profile("local")
public class BootstrapData implements CommandLineRunner {

    private final ExchangeService exchangeService;
    private final CurrencyService currencyService;
    private final ForexService forexService;
    private final StockService stockService;

    @Override
    public void run(String... args) throws Exception {

        // CURRENCY DATA
        List<CurrencyCsvBean> currencyCsvBeanList = getCurrencies();
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");


        // EXCHANGE DATA
        List<ExchangeCSV> exchangeCSVList = getExchangeData();
        exchangeService.persistExchanges(exchangeCSVList);
        System.out.println("Exchange Data loaded");

        //LISTING
        forexService.loadForex();
        System.out.println("Forexes loaded");
        stockService.loadStocks();
        System.out.println("Stocks loaded");
    }

    public List<CurrencyCsvBean> getCurrencies() throws IOException {
        return new CsvToBeanBuilder<CurrencyCsvBean>(new FileReader(ResourceUtils.getFile("exchange-service/csv-files/currencies.csv")))
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }

    public List<ExchangeCSV> getExchangeData() throws FileNotFoundException {

        return new CsvToBeanBuilder<ExchangeCSV>(new FileReader(ResourceUtils.getFile("exchange-service/csv-files/exchange.csv")))
                .withType(ExchangeCSV.class)
                .withSkipLines(1)
                .build()
                .parse();

    }
}
