package de.codecentric.cxf.endpoint;


import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

@RunWith(SpringRunner.class)
public class WeatherServiceEndpointIT {

	@Rule
	public GenericContainer bootApp = new GenericContainer(
			new ImageFromDockerfile()
					.withFileFromFile("Dockerfile", FileUtils.getFile("src", "main", "resources", "docker", "Dockerfile"))
					.withFileFromFile("cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar", FileUtils.getFile("target", "cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar"))
					).withExposedPorts(8080);


	@Rule
	public GenericContainer nginx = new GenericContainer("nginx:alpine")
			.withExposedPorts(80)
			.withClasspathResourceMapping("nginx-location.conf","/etc/nginx/conf.d/nginx-location.conf", BindMode.READ_ONLY)
			;

	@Value(value="classpath:requests/GetCityForecastByZIPTest.xml")
	private Resource GetCityForecastByZIPTestXml;

	@Before
	public void setUp() throws Exception {
		nginx.addLink(bootApp, "boot-app");
	}

	/*
	@Autowired
	private WeatherService weatherServiceClient;

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
	*/

	@Test
	public void test() throws Exception {
		System.out.println("bla");

	}
}
