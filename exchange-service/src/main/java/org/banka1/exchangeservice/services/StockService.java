package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.banka1.exchangeservice.domains.dtos.stock.StockDtoFlask;
import org.banka1.exchangeservice.domains.dtos.stock.StockResponseDtoFlask;
import org.banka1.exchangeservice.domains.dtos.stock.TimeSeriesStockEnum;
import org.banka1.exchangeservice.domains.entities.Exchange;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StockService {

    private final ExchangeRepository exchangeRepository;
    private final StockRepository stockRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${flask.api.stock.timeseries}")
    private String baseTimeSeriesUrl;
    @Value("${flask.api.stock.timeseries.intraday}")
    private String baseTimeSeriesIntraDayUrl;

    public StockService(ExchangeRepository exchangeRepository, StockRepository stockRepository) {
        this.exchangeRepository = exchangeRepository;
        this.stockRepository = stockRepository;
    }

    public void loadStocks() throws IOException, InterruptedException {
        BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile("exchange-service/csv-files/stocks_test.csv")));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();

        Exchange exchange = exchangeRepository.findByExcAcronym("NASDAQ");
        List<Stock> stocksToSave = new ArrayList<>();

        for(CSVRecord record: csvRecords) {
            String symbol = record.get("symbol");

            StockResponseDtoFlask stockResponseDtoFlask = getStockFromFlask(symbol, TimeSeriesStockEnum.DAILY);
            if (stockResponseDtoFlask == null)
                continue;

            Stock stock = new Stock();
            stock.setExchange(exchange);
            stock.setSymbol(symbol);
            updateStockFromFlask(stock,stockResponseDtoFlask);
            stocksToSave.add(stock);
        }
        stockRepository.saveAll(stocksToSave);
    }

    private void updateStocks(List<Stock> stocksToCheckForUpdate) throws IOException, InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        List<Stock> stocks = new ArrayList<>();
        for (Stock stock : stocksToCheckForUpdate){
            Duration duration = Duration.between(stock.getLastRefresh(), now);
            if(duration.toMinutes() > 15){
                try {
                    StockResponseDtoFlask stockResponseDtoFlask = getStockFromFlask(stock.getSymbol(), TimeSeriesStockEnum.DAILY);
                    if(stockResponseDtoFlask != null) {
                        updateStockFromFlask(stock,stockResponseDtoFlask);
                        stocks.add(stock);
                    }
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if(!stocks.isEmpty()) stockRepository.saveAll(stocks);
    }

    public Page<Stock> getStocks(Integer page, Integer size, String symbol){
        Page<Stock> stocks;
        if (symbol == null) {
            stocks = stockRepository.findAll(PageRequest.of(page, size));
        } else {
            stocks = stockRepository.getAllBySymbolContainsIgnoreCase(symbol, PageRequest.of(page, size, Sort.by("symbol").ascending()));
        }

        updateStocks(stocks.getContent());
        return stocks;
    }

    public Optional<Stock> getStockById(Long id){
        if(stockRepository.existsById(id)){
            return stockRepository.findById(id);
        }
        else {
            throw new NotFoundExceptions("stock not found");
        }
    }

    private StockResponseDtoFlask getStockFromFlask(String symbol, TimeSeriesStockEnum timeSeries) throws IOException, InterruptedException {
        String url = switch (timeSeries) {
            case HOUR, FIVE_MIN ->
                    baseTimeSeriesIntraDayUrl + "?symbol=" + symbol + "&interval=" + timeSeries.getValue();
            case DAILY, WEEKLY, MONTHLY ->
                    baseTimeSeriesUrl + "?symbol=" + symbol + "&time_series=" + timeSeries.getValue();
        };

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            return null;

        String jsonStock = response.body();
        return objectMapper.readValue(jsonStock, StockResponseDtoFlask.class);
    }

    private void updateStockFromFlask(Stock stock, StockResponseDtoFlask stockResponseDtoFlask){
        StockDtoFlask stockDtoFlask = stockResponseDtoFlask.getTimeSeries().get(0);
        StockDtoFlask stockDtoFlask2 = stockResponseDtoFlask.getTimeSeries().get(1);

        stock.setLastRefresh(LocalDateTime.now());
        stock.setHigh(stockDtoFlask.getHigh());
        stock.setLow(stockDtoFlask.getLow());
        stock.setOpen(stockDtoFlask.getOpen());
        stock.setClose(stockDtoFlask.getClose());
        stock.setPrice((stockDtoFlask.getHigh() + stockDtoFlask.getLow()) / 2);
        stock.setPriceChange(stockDtoFlask.getHigh() - stockDtoFlask2.getHigh());
        stock.setPriceChangeInPercentage(stockDtoFlask.getHigh()*100/stockDtoFlask2.getHigh()-100);
        stock.setVolume(stockDtoFlask.getVolume());
    }

    public StockResponseDtoFlask getStockTimeSeries(String symbol, TimeSeriesStockEnum timeSeries) {
        try {
            return getStockFromFlask(symbol, timeSeries);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
