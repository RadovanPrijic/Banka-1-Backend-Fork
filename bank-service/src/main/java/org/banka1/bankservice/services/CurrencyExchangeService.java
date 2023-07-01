package org.banka1.bankservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.banka1.bankservice.domains.dtos.currency_exchange.FlaskResponse;
import org.banka1.bankservice.domains.entities.currency_exchange.ExchangePair;
import org.banka1.bankservice.repositories.ExchangePairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CurrencyExchangeService {

    private final ExchangePairRepository exchangePairRepository;
    private final ObjectMapper objectMapper;

    @Value("${flask.api.forex.exchange}")
    private String baseForexUrl;

    public CurrencyExchangeService(ExchangePairRepository exchangePairRepository, @Autowired ObjectMapper objectMapper) {
        this.exchangePairRepository = exchangePairRepository;
        this.objectMapper = objectMapper;
    }

    public void loadForex() throws Exception {
        FileReader fileReader;
        try {
            fileReader = new FileReader(ResourceUtils.getFile("bank-service/csv-files/bank_forex_pairs.csv"));
        } catch (Exception e) {
            fileReader = new FileReader(ResourceUtils.getFile("classpath:csv/bank_forex_pairs.csv"));
        }

        BufferedReader reader = new BufferedReader(fileReader);
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();
        List<ExchangePair> exchangePairsToSave = new ArrayList<>();

        for(CSVRecord record: csvRecords) {
            String from = record.get("from");
            String to = record.get("to");

            String url = baseForexUrl + "?from_currency=" + from + "&to_currency=" + to;
            FlaskResponse flaskResponse = getForexFromFlask(url, FlaskResponse.class);

            ExchangePair exchangePair = new ExchangePair();
            exchangePair.setExchangePairSymbol(from + "/" + to);
            exchangePair.setExchangeRate(flaskResponse.getExchangeRate());
            exchangePairsToSave.add(exchangePair);
        }

        exchangePairRepository.saveAll(exchangePairsToSave);
    }

    private <T> T getForexFromFlask(String url, Class<T> clazz) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200)
            return null;

        String jsonForex = response.body();
        return objectMapper.readValue(jsonForex, clazz);
    }
}
