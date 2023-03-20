package org.banka1.exchangeservice.bootstrap;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.AllArgsConstructor;
import org.banka1.exchangeservice.domains.dtos.CurrencyCsvBean;
import org.banka1.exchangeservice.services.CurrencyService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
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
                getCurrencies("https://www.alphavantage.co/physical_currency_list/");
        currencyService.persistCurrencies(currencyCsvBeanList);
        System.out.println("Currency Data Loaded!");
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
}
