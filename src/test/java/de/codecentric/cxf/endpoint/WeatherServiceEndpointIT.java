package de.codecentric.cxf.endpoint;


import de.codecentric.cxf.common.XmlUtils;
import de.codecentric.namespace.weatherservice.WeatherService;
import de.codecentric.namespace.weatherservice.general.ForecastReturn;
import de.codecentric.namespace.weatherservice.general.GetCityForecastByZIP;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class WeatherServiceEndpointIT {

	public GenericContainer bootapp = new GenericContainer(
			new ImageFromDockerfile()
					.withFileFromFile("Dockerfile", FileUtils.getFile("src", "main", "resources", "docker", "java-service", "Dockerfile"))
					.withFileFromFile("cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar", FileUtils.getFile("target", "cxf-spring-boot-starter-system-tests-1.1.1-SNAPSHOT.jar")))
			.withExposedPorts(8080)
			.waitingFor(Wait.forHttp("/weather/Weather?wsdl"));

	public GenericContainer nginx = new GenericContainer(
			new ImageFromDockerfile()
					.withFileFromFile("Dockerfile", FileUtils.getFile("src", "main", "resources", "docker", "nginx", "Dockerfile"))
					.withFileFromFile("nginx.conf", FileUtils.getFile("src", "main", "resources", "docker", "nginx", "nginx.conf")))
			.withExposedPorts(80)
			.waitingFor(Wait.forHttp("/proxied-weather/Weather?wsdl"));

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

	@Test
    public void client_call_to_service() throws Exception {
        JaxWsProxyFactoryBean jaxWsFactory = new JaxWsProxyFactoryBean();
        jaxWsFactory.setServiceClass(WeatherService.class);
        jaxWsFactory.setAddress("http://" + nginx.getContainerIpAddress() + ":" + nginx.getMappedPort(80) + "/proxied-weather/Weather");
        WeatherService weatherServiceClient = (WeatherService) jaxWsFactory.create();

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
