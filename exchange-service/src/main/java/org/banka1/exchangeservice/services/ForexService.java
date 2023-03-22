package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.ForexResponseDto;
import org.banka1.exchangeservice.domains.dtos.PriceDto;
import org.banka1.exchangeservice.domains.dtos.TimeSeriesForexResponseDto;
import org.banka1.exchangeservice.domains.entities.Forex;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
@Slf4j
public class ForexService {

    private final ForexRepository forexRepository;
    private final CurrencyRepository currencyRepository;
    private final ExchangeRepository exchangeRepository;

    public ForexService(ForexRepository forexRepository, CurrencyRepository currencyRepository, ExchangeRepository exchangeRepository) {
        this.forexRepository = forexRepository;
        this.currencyRepository = currencyRepository;
        this.exchangeRepository = exchangeRepository;
    }

    public void loadForexes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/forex_pairs?currency_base=EUR&format=json"))
                        .header("X-RapidAPI-Key", "e73b08aeecmshb83cd80a2d29f04p1dcc0cjsn13ba5836ac8c")
                        .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        String json = response.body();
        ObjectMapper objectMapper = new ObjectMapper();
        ForexResponseDto forexResponseDto = objectMapper.readValue(json, ForexResponseDto.class);
        System.out.println(forexResponseDto.getData().get(0));
        forexResponseDto.getData().forEach((forexPairDto) -> {

            var currencyBase = currencyRepository.findByCurrencyName(forexPairDto.getCurrency_base()).get();
            var currencyQuote = currencyRepository.findByCurrencyName("United Arab Emirates Dirham").get();

            Forex forex = Forex.builder()
                    .baseCurrency(currencyBase)
                    .quoteCurrency(currencyQuote)
                    .symbol(forexPairDto.getSymbol())
                    .build();

            forexRepository.save(forex);
        });

            HttpRequest requestTimeSeries = HttpRequest.newBuilder()
                    .uri(URI.create("https://twelve-data1.p.rapidapi.com/time_series?interval=15min&symbol=EUR/AED&format=json&outputsize=30"))
                    .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                    .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> responseTimeSeries = HttpClient.newHttpClient()
                    .send(requestTimeSeries, HttpResponse.BodyHandlers.ofString());
            System.out.println(responseTimeSeries.body());

            String jsonTime = responseTimeSeries.body();

                TimeSeriesForexResponseDto timeSeriesResponseDto = objectMapper.readValue(jsonTime, TimeSeriesForexResponseDto.class);
                System.out.println(timeSeriesResponseDto);

                HttpRequest requestPrice = HttpRequest.newBuilder()
                        .uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=EUR/AED&outputsize=30&format=json"))
                        .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                        .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> responsePrice = HttpClient.newHttpClient().send(requestPrice, HttpResponse.BodyHandlers.ofString());
                System.out.println(responsePrice.body());

                String jsonPrice = responsePrice.body();
                PriceDto priceDto = objectMapper.readValue(jsonPrice, PriceDto.class);

                var exchange = exchangeRepository.findByExcId(536L);

                forexRepository.findAll().forEach((forex -> {
                    forex.setHigh(timeSeriesResponseDto.getValues().get(0).getHigh());
                    forex.setLow(timeSeriesResponseDto.getValues().get(0).getLow());
                    forex.setClose(timeSeriesResponseDto.getValues().get(0).getClose());
                    forex.setPrice(priceDto.getPrice());
                    forex.setLastRefresh(LocalDateTime.now());
                    forex.setExchange(exchange);

                    forexRepository.save(forex);
                }));
    }

    //@Scheduled(cron = "0 * * * * ?") treba staviti cron da ide na 15min, ovo nije odradjeno da se ne bi probio dozvoljen broj poziva
    public void timeChanges() throws IOException, InterruptedException {
        HttpRequest requestTimeSeries = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/time_series?interval=15min&symbol=EUR/AED&format=json&outputsize=30"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> responseTimeSeries = HttpClient.newHttpClient()
                .send(requestTimeSeries, HttpResponse.BodyHandlers.ofString());
        System.out.println(responseTimeSeries.body());

        String jsonTime = responseTimeSeries.body();
        ObjectMapper objectMapper = new ObjectMapper();

        TimeSeriesForexResponseDto timeSeriesResponseDto = objectMapper.readValue(jsonTime, TimeSeriesForexResponseDto.class);
        System.out.println(timeSeriesResponseDto);

        HttpRequest requestPrice = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=EUR/AED&outputsize=30&format=json"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> responsePrice = HttpClient.newHttpClient().send(requestPrice, HttpResponse.BodyHandlers.ofString());
        System.out.println(responsePrice.body());

        String jsonPrice = responsePrice.body();
        PriceDto priceDto = objectMapper.readValue(jsonPrice, PriceDto.class);

        var exchange = exchangeRepository.findByExcId(536L);

        forexRepository.findAll().forEach((forex -> {
            forex.setHigh(timeSeriesResponseDto.getValues().get(0).getHigh());
            forex.setLow(timeSeriesResponseDto.getValues().get(0).getLow());
            forex.setClose(timeSeriesResponseDto.getValues().get(0).getClose());
            forex.setPrice(priceDto.getPrice());
            forex.setLastRefresh(LocalDateTime.now());
            forex.setExchange(exchange);

            forexRepository.save(forex);
        }));
    }
}
