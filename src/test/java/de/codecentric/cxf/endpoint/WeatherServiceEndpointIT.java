package de.codecentric.cxf.endpoint;


import de.codecentric.cxf.common.XmlUtils;
import de.codecentric.namespace.weatherservice.WeatherService;
import de.codecentric.namespace.weatherservice.general.ForecastReturn;
import de.codecentric.namespace.weatherservice.general.GetCityForecastByZIP;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
public class WeatherServiceEndpointIT {

    public static final String HTTP_HOST = "http://localhost:80";

    @Value(value = "classpath:requests/GetCityForecastByZIPTest.xml")
    private Resource GetCityForecastByZIPTestXml;

	@Test
    public void client_call_to_service() throws Exception {
        JaxWsProxyFactoryBean jaxWsFactory = new JaxWsProxyFactoryBean();
        jaxWsFactory.setServiceClass(WeatherService.class);
        jaxWsFactory.setAddress(HTTP_HOST + "/proxied-weather/Weather");
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
