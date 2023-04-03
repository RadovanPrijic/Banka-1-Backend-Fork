package org.banka1.exchangeservice.cucumber.currencyservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(value = "/application-test_it.properties")
@ActiveProfiles("test_it")
public class CurrencyServiceTestsConfig {
}
