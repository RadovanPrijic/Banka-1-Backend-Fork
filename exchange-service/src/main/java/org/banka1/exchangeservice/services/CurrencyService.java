package org.banka1.exchangeservice.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.domains.dtos.CurrencyCsvBean;
import org.banka1.exchangeservice.domains.entities.Currency;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<CurrencyCsvBean> getCurrencies(String fileUrl) throws IOException {
        FileOutputStream fileOutputStream = null;
        try {
            URL currencyListUrl = new URL(fileUrl);
            ReadableByteChannel readableByteChannel = Channels.newChannel(currencyListUrl.openStream());
            fileOutputStream = new FileOutputStream("currencies.csv");
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null)
                fileOutputStream.close();
        }

        return new CsvToBeanBuilder<CurrencyCsvBean>(new FileReader("currencies.csv"))
                .withType(CurrencyCsvBean.class)
                .withSkipLines(1)
                .build()
                .parse();
    }

    public void persistCurrencies(List<CurrencyCsvBean> currencyCsvBeanList) {

        for(CurrencyCsvBean currencyCsvBean : currencyCsvBeanList) {
            Currency currency = new Currency();

            currency.setCurrencyCode(currencyCsvBean.getCurrencyCode());
            currency.setCurrencyName(currencyCsvBean.getCurrencyName());

            try {
                java.util.Currency c = java.util.Currency.getInstance(currency.getCurrencyCode());
                if (c != null)
                    currency.setCurrencySymbol(c.getSymbol(Locale.US));

                Locale locale = new Locale("", currency.getCurrencyCode().substring(0, 2));
                currency.setPolity(locale.getDisplayCountry(Locale.US));

            } catch (Exception e) {
                e.printStackTrace();
            }
            currencyRepository.save(currency);
        }
    }
}

