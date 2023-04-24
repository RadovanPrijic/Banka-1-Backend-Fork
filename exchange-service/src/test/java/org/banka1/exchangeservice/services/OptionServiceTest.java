package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.domains.dtos.option.OptionDto;
import org.banka1.exchangeservice.domains.dtos.option.OptionFilterRequest;
import org.banka1.exchangeservice.domains.entities.Option;
import org.banka1.exchangeservice.domains.entities.OptionType;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.domains.mappers.OptionMapper;
import org.banka1.exchangeservice.repositories.OptionRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.function.Predicate;

import static org.mockito.Mockito.*;

public class OptionServiceTest {

    private OptionRepository optionRepository;
    private StockRepository stockRepository;
    private OptionService optionService;

    @BeforeEach
    public void setUp(){
        stockRepository = mock(StockRepository.class);
        optionRepository = mock(OptionRepository.class);
        optionService = new OptionService(optionRepository, stockRepository);
    }

    @Test
    public void getOptionsSuccessfully () {
        //given
        OptionFilterRequest optionFilterRequest = new OptionFilterRequest();
        optionFilterRequest.setSymbol("AAPL");

        //when
        when(optionRepository.findAll(optionFilterRequest.getPredicate())).thenReturn(getOptions());
        when(stockRepository.findBySymbol(optionFilterRequest.getSymbol())).thenReturn(getStock());
        List<OptionDto> dtos = optionService.getOptions(optionFilterRequest);

        //then
        assertThat(dtos.size()).isEqualTo(10);

        //verify
        verify(optionRepository, times(1)).findAll(optionFilterRequest.getPredicate());
        verifyNoMoreInteractions(optionRepository);
        verify(stockRepository, times(1)).findBySymbol(optionFilterRequest.getSymbol());
        verifyNoMoreInteractions(stockRepository);


    }

    private Iterable<Option> getOptions(){
        List<Option> options = new ArrayList<>();
        options.add(createOption("AAPL", 200.0, OptionType.CALL, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 190.0, OptionType.CALL, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 170.0, OptionType.CALL, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 175.0, OptionType.CALL, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 165.0, OptionType.CALL, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 200.0, OptionType.PUT, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 190.0, OptionType.PUT, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 170.0, OptionType.PUT, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 175.0, OptionType.PUT, LocalDate.of(2023, 4, 20)));
        options.add(createOption("AAPL", 165.0, OptionType.PUT, LocalDate.of(2023, 4, 20)));
        return options;
    }

    private Option createOption(String symbol, Double strike, OptionType optionType, LocalDate expirationType) {
        return Option.builder()
                .symbol(symbol)
                .strike(strike)
                .optionType(optionType)
                .expirationDate(expirationType)
                .build();
    }

    private Stock getStock(){
        Stock stock = new Stock();
        stock.setSymbol("AAPL");
        stock.setPrice(100D);
        return stock;
    }
}
