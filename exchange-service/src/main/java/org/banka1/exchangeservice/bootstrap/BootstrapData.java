package org.banka1.exchangeservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.ExchangeCSV;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


@Component
@AllArgsConstructor
@Profile("local")
public class BootstrapData implements CommandLineRunner {

    private final ExchangeRepository exchangeRepository;
    private final CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {
        String exchangeCSVPath = "classpath:exchange.csv";
    
        System.out.println("Loading Currency Data ...");


        List<CurrencyCsvBean> currencyCsvBeanList =
                getCurrencies("https://www.alphavantage.co/physical_currency_list/");
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");
        
        
        List<Exchange> exchanges = new ArrayList<>();

        List<ExchangeCSV> exchangeCSV = new CsvToBeanBuilder<ExchangeCSV>(new FileReader(ResourceUtils.getFile(exchangeCSVPath)))
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
        System.out.println("Exchange Data loaded");
        
    }

    public List<CurrencyCsvBean> getCurrencies(String fileUrl) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            URL currencyListUrl = new URL(fileUrl);
            ReadableByteChannel readableByteChannel = Channels.newChannel(currencyListUrl.openStream());
            fileOutputStream = new FileOutputStream("currencies.csv");
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null)
                fileOutputStream.close();
        }

        return new CsvToBeanBuilder<CurrencyCsvBean>(new FileReader("currencies.csv"))
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }
}
