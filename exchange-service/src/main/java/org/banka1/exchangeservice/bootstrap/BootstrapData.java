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

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("local")
public class BootstrapData implements CommandLineRunner {

    //Fajl se nalazi u resource folderu
    private String exchangeCSVPath = "classpath:exchange.csv";

    private final ExchangeRepository exchangeRepository;

    public BootstrapData(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing data...");
        //Proba za unos u bazu hardkodirano

//        Exchange exchange1 = new Exchange(null,"Jakarta Futures Exchange (bursa Berjangka Jakarta)","BBJ","XBBJ","Indonesia","Indonesian Rupiah","Asia/Jakarta", "09:00", "17:30");
//        Exchange exchange2 = new Exchange(null,"Asx - Trade24","SFE","XSFE","Australia","Australian Dollar","Australia/Melbourne", "10:00", "16:00");
//        exchangeRepository.save(exchange1);
//        exchangeRepository.save(exchange2);


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
        System.out.println("Data loaded");
    }
}
