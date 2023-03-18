package org.banka1.exchangeservice.bootstrap;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Profile("local")
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Currency Data ...");
        List<CurrencyCsvBean> currencyCsvBeanList =
                currencyService.getCurrencies("https://www.alphavantage.co/physical_currency_list/");
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");
    }
}
