package org.banka1.exchangeservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.ForexFilterRequest;
import org.banka1.exchangeservice.domains.dtos.ForexResponseDto;
import org.banka1.exchangeservice.domains.dtos.PriceDto;
import org.banka1.exchangeservice.domains.dtos.TimeSeriesForexResponseDto;
import org.banka1.exchangeservice.domains.entities.Forex;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.banka1.exchangeservice.repositories.ExchangeRepository;
import org.banka1.exchangeservice.repositories.ForexRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public Page<Forex> getForexes(Integer page, Integer size, ForexFilterRequest forexFilterRequest){
        Page<Forex> forexes = forexRepository.findAll(forexFilterRequest.getPredicate(), PageRequest.of(page,size));
        return forexes;
    }

}
