package org.banka1.exchangeservice.cucumber.exchangeservice;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/exchange-service-tests.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.banka1.exchangeservice.cucumber.exchangeservice")
public class ExchangeServiceTests {
}
