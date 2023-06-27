package org.banka1.bankservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.bankservice.domains.dtos.currency.CurrencyCsvBean;
import org.banka1.bankservice.domains.entities.Currency;
import org.banka1.bankservice.domains.entities.Status;
import org.banka1.bankservice.domains.exceptions.NotFoundException;
import org.banka1.bankservice.domains.mappers.CurrencyMapper;
import org.banka1.bankservice.repositories.CurrencyRepository;
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
                currency.setCountry(locale.getDisplayCountry(Locale.US));
                currency.setStatus(Status.ACTIVE);

            } catch (Exception e) {
                currency.setCurrencySymbol("Doesn't exist");
                currency.setCountry("Doesn't exist");
            }

            currenciesToSave.add(currency);
        }

        currencyRepository.saveAll(currenciesToSave);
    }

    public CurrencyCsvBean findCurrencyByCurrencyName(String currencyName) {
        Optional<Currency> currency = currencyRepository.findByCurrencyName(currencyName);
        return currency.map(CurrencyMapper.INSTANCE::currencyToCurrencyCsvBean).orElseThrow(() -> new NotFoundException("currency not found"));
    }
}
