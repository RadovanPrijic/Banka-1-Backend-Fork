package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.domains.dtos.option.OptionDto;
import org.banka1.exchangeservice.domains.dtos.option.OptionFilterRequest;
import org.banka1.exchangeservice.domains.entities.Option;
import org.banka1.exchangeservice.domains.entities.OptionType;
import org.banka1.exchangeservice.domains.entities.Stock;
import org.banka1.exchangeservice.domains.mappers.OptionMapper;
import org.banka1.exchangeservice.repositories.OptionRepository;
import org.banka1.exchangeservice.repositories.StockRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OptionService {

    private final OptionRepository optionRepository;
    private final StockRepository stockRepository;

    public OptionService(OptionRepository optionRepository, StockRepository stockRepository) {
        this.optionRepository = optionRepository;
        this.stockRepository = stockRepository;
    }

    public void loadOptions(){
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

        options.add(createOption("AMZN", 220.0, OptionType.CALL, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 220.0, OptionType.CALL, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 220.0, OptionType.CALL, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 180.0, OptionType.CALL, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 180.0, OptionType.CALL, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 220.0, OptionType.PUT, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 220.0, OptionType.PUT, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 220.0, OptionType.PUT, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 180.0, OptionType.PUT, LocalDate.of(2023, 4, 22)));
        options.add(createOption("AMZN", 180.0, OptionType.PUT, LocalDate.of(2023, 4, 22)));

        options.add(createOption("TSLA", 230.0, OptionType.CALL, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 230.0, OptionType.CALL, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 150.0, OptionType.CALL, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 150.0, OptionType.CALL, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 170.0, OptionType.CALL, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 230.0, OptionType.PUT, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 230.0, OptionType.PUT, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 150.0, OptionType.PUT, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 150.0, OptionType.PUT, LocalDate.of(2023, 4, 24)));
        options.add(createOption("TSLA", 170.0, OptionType.PUT, LocalDate.of(2023, 4, 24)));


        optionRepository.saveAll(options);
    }


    public List<OptionDto> getOptions(OptionFilterRequest optionFilterRequest) {
        Iterable<Option> optionIterable = optionRepository.findAll(optionFilterRequest.getPredicate());
        List<Option> options = new ArrayList<>();
        optionIterable.forEach(options::add);

        Stock stock = stockRepository.findBySymbol(optionFilterRequest.getSymbol());
        options.forEach(o -> {
           o.setAsk(stock.getPrice());
           o.setBid(stock.getPrice());
           o.setPrice(stock.getPrice());
        });

        Map<OptionDto, List<Option>> optionMap = options.stream()
                .collect(Collectors.groupingBy(o -> OptionDto.builder().strike(o.getStrike()).optionType(o.getOptionType()).build()));

        List<OptionDto> optionsToReturn = new ArrayList<>();
        for (Map.Entry<OptionDto, List<Option>> entry: optionMap.entrySet()) {
            OptionDto optionDto = entry.getKey();
            List<Option> optionList = entry.getValue();

            optionDto.setOpenInterest(optionList.size());
            OptionMapper.INSTANCE.updateOptionDto(optionDto, optionList.get(0));
            optionsToReturn.add(optionDto);
        }

        return optionsToReturn;
    }

    private Option createOption(String symbol, Double strike, OptionType optionType, LocalDate expirationType) {
        return Option.builder()
                .symbol(symbol)
                .strike(strike)
                .optionType(optionType)
                .expirationDate(expirationType)
                .build();
    }
}
