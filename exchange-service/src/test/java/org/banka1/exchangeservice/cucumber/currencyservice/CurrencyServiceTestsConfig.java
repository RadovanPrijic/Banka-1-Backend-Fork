package org.banka1.exchangeservice.cucumber.currencyservice;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@Profile("local")
@TestPropertySource(value = "/application-test_it-local.properties")
public class CurrencyServiceTestsConfig {
}
