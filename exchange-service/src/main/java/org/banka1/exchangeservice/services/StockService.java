package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
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
    private final ObjectMapper objectMapper;

    @Value("${flask.api.stock.timeseries}")
    private String baseTimeSeriesUrl;
    @Value("${flask.api.stock.timeseries.intraday}")
    private String baseTimeSeriesIntraDayUrl;

    private final RabbitTemplate rabbitTemplate;
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.routing.stock.key}")
    private String routingKey;

    public StockService(ExchangeRepository exchangeRepository, StockRepository stockRepository,
                        @Autowired RabbitTemplate rabbitTemplate, @Autowired ObjectMapper objectMapper) {
        this.exchangeRepository = exchangeRepository;
        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void loadStocks() throws IOException, InterruptedException {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("exchange-service/csv-files/stocks_test.csv"));
        } catch (Exception e) {
            fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/stocks_test.csv"));
        }

        BufferedReader reader = new BufferedReader(fileReader);
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

    public void updateStocks(List<Stock> stocksToCheckForUpdate) {
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

    @Scheduled(cron = "0 0/1 * * * *")
    public void refreshStockData() {
        List<Stock> stocks = stockRepository.findAll();

        stocks.forEach(stock -> {
            try {
                StockResponseDtoFlask stockResponseDtoFlask = getStockFromFlask(stock.getSymbol(), TimeSeriesStockEnum.DAILY);
                if(stockResponseDtoFlask != null) {
                    stock.setLastRefresh(LocalDateTime.now());
                    updateStockFromFlask(stock,stockResponseDtoFlask);
                    stockRepository.save(stock);
                    rabbitTemplate.convertAndSend(exchange, routingKey, stock);
                }
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
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

    public Stock getStockBySymbol(String symbol){
        if(stockRepository.existsStockBySymbol(symbol))
            return stockRepository.findBySymbol(symbol);
        else
            throw new NotFoundExceptions("Stock with the symbol " + symbol + " has not been found.");
    }

    public List<String> getStockSymbols(){
        Iterable<Stock> stockIterable = stockRepository.findAll();
        List<String> symbols = new ArrayList<>();

        for (Stock stock : stockIterable) {
            if(!symbols.contains(stock.getSymbol()))
                symbols.add(stock.getSymbol());
        }

        return symbols;
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
