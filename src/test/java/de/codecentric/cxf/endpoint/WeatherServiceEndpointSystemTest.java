package de.codecentric.cxf.endpoint;


import de.codecentric.cxf.TestApplication;
import de.codecentric.cxf.common.BootStarterCxfException;
import de.codecentric.cxf.common.XmlUtils;
import de.codecentric.namespace.weatherservice.WeatherException;
import de.codecentric.namespace.weatherservice.WeatherService;
import de.codecentric.namespace.weatherservice.general.ForecastReturn;
import de.codecentric.namespace.weatherservice.general.GetCityForecastByZIP;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.testcontainers.containers.BindMode.READ_ONLY;

@RunWith(SpringRunner.class)
@SpringBootTest(
		classes = TestApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
		properties = { "server.port:8087" }
)
public class WeatherServiceEndpointSystemTest {

	@ClassRule
	public static GenericContainer nginx = new GenericContainer("nginx:alpine")
			.withExposedPorts(80)
			.withClasspathResourceMapping("nginx-location.conf","/etc/nginx/conf.d/nginx-location.conf", READ_ONLY)
			;

	@Autowired
	private WeatherService weatherServiceClient;

	@Value(value="classpath:requests/GetCityForecastByZIPTest.xml")
	private Resource GetCityForecastByZIPTestXml;
	
	@Test
	public void isEndpointCorrectlyAutoDetectedAndConfigured() throws WeatherException, BootStarterCxfException, IOException {
		// Given
		GetCityForecastByZIP getCityForecastByZIP = XmlUtils.readSoapMessageFromStreamAndUnmarshallBody2Object(
				GetCityForecastByZIPTestXml.getInputStream(), GetCityForecastByZIP.class);
		
		// When
		ForecastReturn forecastReturn = weatherServiceClient.getCityForecastByZIP(getCityForecastByZIP.getForecastRequest());
		
		// Then
		assertNotNull(forecastReturn);
		assertEquals("Weimar", forecastReturn.getCity());
		assertEquals("22%", forecastReturn.getForecastResult().getForecast().get(0).getProbabilityOfPrecipiation().getDaytime());
	}
}
