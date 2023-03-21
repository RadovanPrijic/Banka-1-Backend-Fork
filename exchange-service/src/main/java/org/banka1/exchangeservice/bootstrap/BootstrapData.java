package org.banka1.exchangeservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import org.banka1.exchangeservice.domains.dtos.ExchangeCSV;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import java.util.ArrayList;


@Component
@AllArgsConstructor
@Profile("local")
public class BootstrapData implements CommandLineRunner {

    private final ExchangeRepository exchangeRepository;
    private final CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {

        // CURRENCY DATA
        List<CurrencyCsvBean> currencyCsvBeanList = getCurrencies();
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");


        // EXCHANGE DATA
        loadExchangeData();
        System.out.println("Exchange Data loaded");
        
    }

    public List<CurrencyCsvBean> getCurrencies() throws IOException {
        return new CsvToBeanBuilder<CurrencyCsvBean>(new FileReader(ResourceUtils.getFile("classpath:csv/currencies.csv")))
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }

    public void loadExchangeData() throws FileNotFoundException {
        List<Exchange> exchanges = new ArrayList<>();

        List<ExchangeCSV> exchangeCSV = new CsvToBeanBuilder<ExchangeCSV>(new FileReader(ResourceUtils.getFile("classpath:csv/exchange.csv")))
                .withType(ExchangeCSV.class)
                .withSkipLines(1)
                .build()
                .parse();

        for(ExchangeCSV csv : exchangeCSV) {

            Exchange exchange = Exchange.builder()
                    .excName(csv.getExchangeName())
                    .excAcronym(csv.getExchangeAcronym())
                    .excMicCode(csv.getExchangeMicCode())
                    .excCountry(csv.getCountry())
                    .excCurrency(csv.getCurrency())
                    .excTimeZone(csv.getTimeZone())
                    .excOpenTime(csv.getOpenTime())
                    .excCloseTime(csv.getCloseTime())
                    .build();

            exchanges.add(exchange);
        }
        exchangeRepository.saveAll(exchanges);
    }
}
