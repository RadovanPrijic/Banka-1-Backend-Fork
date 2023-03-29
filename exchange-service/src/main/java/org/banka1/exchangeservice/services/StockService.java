package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.banka1.exchangeservice.domains.dtos.StockDtoFlask;
import org.banka1.exchangeservice.domains.dtos.StockResponseDtoFlask;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class StockService {

    private final ExchangeRepository exchangeRepository;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StockService(ExchangeRepository exchangeRepository, StockRepository stockRepository) {
        this.exchangeRepository = exchangeRepository;
        this.stockRepository = stockRepository;
    }

    public void loadStocks() throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:csv/stocks.csv")));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();

        Exchange exchange = exchangeRepository.findByExcAcronym("NASDAQ");
        List<Stock> stocksToSave = new ArrayList<>();

        for(CSVRecord record: csvRecords) {
            String symbol = record.get("symbol");
            String url = "http://127.0.0.1:8888/stocks/time-series?symbol=" + symbol + "&time_series=TIME_SERIES_DAILY";
            System.out.println(url);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200)
                continue;

            String jsonStock = response.body();
            StockResponseDtoFlask stockResponseDtoFlask = objectMapper.readValue(jsonStock, StockResponseDtoFlask.class);
            StockDtoFlask stockDtoFlask = stockResponseDtoFlask.getTimeSeries().get(0);
            StockDtoFlask stockDtoFlask2 = stockResponseDtoFlask.getTimeSeries().get(1);
            Stock stock = new Stock();
            stock.setExchange(exchange);
            stock.setSymbol(symbol);
            stock.setLastRefresh(LocalDateTime.now());
            stock.setHigh(stockDtoFlask.getHigh());
            stock.setLow(stockDtoFlask.getLow());
            stock.setOpen(stockDtoFlask.getOpen());
            stock.setClose(stockDtoFlask.getClose());
            stock.setPrice((stockDtoFlask.getHigh() + stockDtoFlask.getLow()) / 2);
            stock.setPriceChange(stockDtoFlask.getHigh() - stockDtoFlask2.getHigh());
            stock.setPriceChangeInPercentage(stockDtoFlask.getHigh()*100/stockDtoFlask2.getHigh()-100);
            stock.setVolume(stockDtoFlask.getVolume());
            //juceCena:100 = danasCena:x,  danasCena*100 = x*juceCena,   x=(danasCena*100/juceCena)-100

            stocksToSave.add(stock);
        }
        stockRepository.saveAll(stocksToSave);
    }
}
