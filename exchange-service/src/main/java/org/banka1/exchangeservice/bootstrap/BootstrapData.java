package org.banka1.exchangeservice.bootstrap;

import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("local")
public class BootstrapData implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {
        System.err.println("Loading Currency Data ...");

        currencyService.persistCurrencies();
    }
}
