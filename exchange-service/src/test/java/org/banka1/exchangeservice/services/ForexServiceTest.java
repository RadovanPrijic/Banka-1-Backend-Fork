package org.banka1.exchangeservice.services;

import org.banka1.exchangeservice.IntegrationTest;
import org.banka1.exchangeservice.domains.dtos.forex.ForexFilterRequest;
import org.banka1.exchangeservice.domains.entities.Forex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class ForexServiceTest extends IntegrationTest {

    @Autowired
    private ForexService forexService;

    @Test
    public void getForexesByFilterRequestTest() throws Exception {
        forexService.loadForex();

        ForexFilterRequest forexFilterRequest = new ForexFilterRequest();

        Page<Forex> forexes = forexService.getForexes(0, 10, forexFilterRequest);
        Assertions.assertNotNull(forexes);
        Assertions.assertEquals(9, forexes.getTotalElements());
    }

}
