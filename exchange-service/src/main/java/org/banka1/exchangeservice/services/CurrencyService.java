package org.banka1.exchangeservice.services;

import lombok.extern.slf4j.Slf4j;
import org.banka1.exchangeservice.repositories.CurrencyRepository;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void persistCurrencies() throws IOException {
        String FILE_URL = "https://www.alphavantage.co/physical_currency_list/";
        FileOutputStream fileOutputStream = null;
        try {
            URL currencyListUrl = new URL(FILE_URL);
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




    }

    public void persistCurrencyInflationRates(){

    }
}

