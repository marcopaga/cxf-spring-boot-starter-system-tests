package de.codecentric.cxf.endpoint;


import de.codecentric.cxf.TestServiceSystemTestConfiguration;
import de.codecentric.namespace.weatherservice.WeatherService;
import org.apache.commons.io.FileUtils;
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
			.waitingFor(Wait.forHttp("/proxied-weather/Weather?wsdl"))
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
	public void justMakeSureTheDockerContainersComeUpAsExpected() throws Exception {
		// The Boot-App and the reverse proxy are up and respond to WSDL requests
	}

}
