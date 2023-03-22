package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.PriceDto;
import org.banka1.exchangeservice.domains.dtos.StockResponseDto;
import org.banka1.exchangeservice.domains.dtos.TimeSeriesStockResponseDto;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

@Service
@Slf4j
public class StockService {

    private final ExchangeRepository exchangeRepository;
    private final StockRepository stockRepository;

    public StockService(ExchangeRepository exchangeRepository, StockRepository stockRepository) {
        this.exchangeRepository = exchangeRepository;
        this.stockRepository = stockRepository;
    }

    public void loadStocks() throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/stocks?country=USA&exchange=NASDAQ&format=json"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        String jsonStock = response.body();
        ObjectMapper objectMapper = new ObjectMapper();

        StockResponseDto stockResponseDto = objectMapper.readValue(jsonStock, StockResponseDto.class);
        var exchange = exchangeRepository.findByExcId(536L);

        HttpRequest requestTime = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/time_series?interval=15min&symbol=AACG&format=json&outputsize=30"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> responseTime = HttpClient.newHttpClient().send(requestTime, HttpResponse.BodyHandlers.ofString());

        String jsonTime = responseTime.body();
        TimeSeriesStockResponseDto timeSeriesStockResponseDto = objectMapper.readValue(jsonTime, TimeSeriesStockResponseDto.class);

        HttpRequest requestPrice = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=AACG&outputsize=30&format=json"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> responsePrice = HttpClient.newHttpClient().send(requestPrice, HttpResponse.BodyHandlers.ofString());

        String jsonPrice = responsePrice.body();
        PriceDto priceDto = objectMapper.readValue(jsonPrice, PriceDto.class);

        stockResponseDto.getData().forEach(stockDto -> {
            Stock stock = Stock.builder()
                    .symbol(stockDto.getSymbol())
                    .exchange(exchange)
                    .build();

            stock.setHigh(timeSeriesStockResponseDto.getValues().get(0).getHigh());
            stock.setLow(timeSeriesStockResponseDto.getValues().get(0).getLow());
            stock.setLow(timeSeriesStockResponseDto.getValues().get(0).getLow());
            stock.setClose(timeSeriesStockResponseDto.getValues().get(0).getClose());
            stock.setVolume(timeSeriesStockResponseDto.getValues().get(0).getVolume());
            stock.setPrice(priceDto.getPrice());
            stock.setLastRefresh(LocalDateTime.now());

            stockRepository.save(stock);
        });
    }

    //@Scheduled(cron = "0 * * * * ?") treba staviti cron da ide na 15min, ovo nije odradjeno da se ne bi probio dozvoljen broj poziva
    public void timeChanges() throws IOException, InterruptedException {
        HttpRequest requestTime = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/time_series?interval=15min&symbol=AACG&format=json&outputsize=30"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> responseTime = HttpClient.newHttpClient().send(requestTime, HttpResponse.BodyHandlers.ofString());

        String jsonTime = responseTime.body();
        ObjectMapper objectMapper = new ObjectMapper();
        TimeSeriesStockResponseDto timeSeriesStockResponseDto = objectMapper.readValue(jsonTime, TimeSeriesStockResponseDto.class);

        HttpRequest requestPrice = HttpRequest.newBuilder()
                .uri(URI.create("https://twelve-data1.p.rapidapi.com/price?symbol=AACG&outputsize=30&format=json"))
                .header("X-RapidAPI-Key", "cd237bacbbmsh1546e6d11927036p123120jsn3db518ba1ec7")
                .header("X-RapidAPI-Host", "twelve-data1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> responsePrice = HttpClient.newHttpClient().send(requestPrice, HttpResponse.BodyHandlers.ofString());

        String jsonPrice = responsePrice.body();
        PriceDto priceDto = objectMapper.readValue(jsonPrice, PriceDto.class);

        stockRepository.findAll().forEach(stock -> {

            stock.setHigh(timeSeriesStockResponseDto.getValues().get(0).getHigh());
            stock.setLow(timeSeriesStockResponseDto.getValues().get(0).getLow());
            stock.setLow(timeSeriesStockResponseDto.getValues().get(0).getLow());
            stock.setClose(timeSeriesStockResponseDto.getValues().get(0).getClose());
            stock.setVolume(timeSeriesStockResponseDto.getValues().get(0).getVolume());
            stock.setPrice(priceDto.getPrice());
            stock.setLastRefresh(LocalDateTime.now());

            stockRepository.save(stock);
        });
    }
}
