package it.alera.airquality;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import it.alera.airquality.client.OpenAqClient;

@SpringBootApplication
public class AirQualityDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirQualityDashboardApplication.class, args);
	}

}
