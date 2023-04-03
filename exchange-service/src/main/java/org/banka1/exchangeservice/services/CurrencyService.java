package org.banka1.exchangeservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.entities.Currency;
import org.banka1.exchangeservice.domains.exceptions.NotFoundExceptions;
import org.banka1.exchangeservice.domains.mappers.CurrencyMapper;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void persistCurrencies(List<CurrencyCsvBean> currencyCsvBeanList) {

        List<Currency> currenciesToSave = new ArrayList<>();

        for(CurrencyCsvBean currencyCsvBean : currencyCsvBeanList) {
            Currency currency = new Currency();

            currency.setCurrencyCode(currencyCsvBean.getCurrencyCode());
            currency.setCurrencyName(currencyCsvBean.getCurrencyName());

            java.util.Currency c;
            try {
                c = java.util.Currency.getInstance(currency.getCurrencyCode());
                if (c != null)
                    currency.setCurrencySymbol(c.getSymbol(Locale.US));

                Locale locale = new Locale("", currency.getCurrencyCode().substring(0, 2));
                currency.setPolity(locale.getDisplayCountry(Locale.US));

            } catch (Exception e) {
                currency.setCurrencySymbol("Doesn't exist");
                currency.setPolity("Doesn't exist");
            }

            currenciesToSave.add(currency);
        }

        currencyRepository.saveAll(currenciesToSave);
    }

    public CurrencyCsvBean findCurrencyByCurrencyName(String currencyName) {
        Optional<Currency> currency = currencyRepository.findByCurrencyName(currencyName);
        return currency.map(CurrencyMapper.INSTANCE::currencyToCurrencyCsvBean).orElseThrow(() -> new NotFoundExceptions("currency not found"));
    }
}

