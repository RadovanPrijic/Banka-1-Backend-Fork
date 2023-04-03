package org.banka1.exchangeservice.cucumber.forexservice;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/forex-service-tests.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "org.banka1.exchangeservice.cucumber.forexservice")
public class ForexServiceTests {
}
