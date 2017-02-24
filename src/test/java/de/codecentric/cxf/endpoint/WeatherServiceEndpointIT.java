package de.codecentric.cxf.endpoint;


import de.codecentric.cxf.TestServiceSystemTestConfiguration;
import de.codecentric.cxf.common.XmlUtils;
import de.codecentric.namespace.weatherservice.WeatherService;
import de.codecentric.namespace.weatherservice.general.ForecastReturn;
import de.codecentric.namespace.weatherservice.general.GetCityForecastByZIP;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@Import(TestServiceSystemTestConfiguration.class)
@EnableAutoConfiguration
@PropertySource("classpath:application-client-mode.properties")
public class WeatherServiceEndpointIT {

	public GenericContainer bootapp = new GenericContainer(
			new ImageFromDockerfile()
					.withFileFromFile("Dockerfile", FileUtils.getFile("src", "main", "resources", "docker", "Dockerfile"))
					.withFileFromFile("cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar", FileUtils.getFile("target", "cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar"))
	)
			.withExposedPorts(8080)
			.waitingFor(Wait.forHttp("/weather/Weather?wsdl"));

	public GenericContainer nginx = new GenericContainer("nginx:alpine")
			.withClasspathResourceMapping("nginx.conf", "/etc/nginx/nginx.conf", BindMode.READ_ONLY)
			.withExposedPorts(80)
			.waitingFor(Wait.forHttp("/weather/Weather?wsdl"))
			;

	@Value(value="classpath:requests/GetCityForecastByZIPTest.xml")
	private Resource GetCityForecastByZIPTestXml;

	@Before
	public void setUp() throws Exception {
		nginx.addLink(bootapp, "bootapp");
		bootapp.start();
		nginx.start();
	}

	@After
	public void tearDown() throws Exception {
		nginx.stop();
		bootapp.stop();
	}

	@Autowired
	private WeatherService weatherServiceClient;

	@Test
	public void isEndpointCorrectlyAutoDetectedAndConfigured() throws Exception {
		System.out.println("Boot-App IP: " + bootapp.getContainerIpAddress());
		System.out.println("Boot-App Port: " + bootapp.getMappedPort(8080));

		System.out.println("Nginx IP: " + nginx.getContainerIpAddress());
		System.out.println("Nginx Port: " + nginx.getMappedPort(80));
		IOUtils.readLines(System.in);

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
