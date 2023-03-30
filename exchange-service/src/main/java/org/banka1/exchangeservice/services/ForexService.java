package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.banka1.exchangeservice.domains.dtos.forex.ForexFilterRequest;
import org.banka1.exchangeservice.domains.dtos.forex.ForexResponseExchangeFlask;
import org.banka1.exchangeservice.domains.entities.Currency;
import org.banka1.exchangeservice.domains.entities.Forex;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ForexService {

    private final ForexRepository forexRepository;
    private final CurrencyRepository currencyRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${flask.api.forex.exchange}")
    private String baseForexUrl;
    @Value("${flask.api.forex.timeseries}")
    private String baseForexTimeSeriesUrl;
    @Value("${flask.api.forex.timeseries.intraday}")
    private String baseForexTimeSeriesIntraDayUrl;

    public ForexService(ForexRepository forexRepository, CurrencyRepository currencyRepository) {
        this.forexRepository = forexRepository;
        this.currencyRepository = currencyRepository;
    }

    public Page<Forex> getForexes(Integer page, Integer size, ForexFilterRequest forexFilterRequest){
        Page<Forex> forexes = forexRepository.findAll(forexFilterRequest.getPredicate(), PageRequest.of(page,size));
        return forexes;
    }

    public void loadForex() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(ResourceUtils.getFile("classpath:csv/forex-pair-test.csv")));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());

        List<CSVRecord> csvRecords = csvParser.getRecords();

        Map<String, Currency> currencyMap = currencyRepository.findAll().stream()
                .collect(Collectors.toMap(Currency::getCurrencyCode, Function.identity()));

        for(CSVRecord record: csvRecords) {
            String from = record.get("from");
            String to = record.get("to");

            Forex forex = new Forex();
            forex.setFromCurrency(currencyMap.get(from));
            forex.setToCurrency(currencyMap.get(to));
            forex.setSymbol(from + "/" + to);

            String url = baseForexUrl + "?from_currency=" + from + "&to_currency=" + to;
            ForexResponseExchangeFlask forexResponseExchangeFlask = getForexFromFlask(url, ForexResponseExchangeFlask.class);
            System.out.println(forexResponseExchangeFlask);
        }
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
