package it.alera.airquality.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class WebClientConfig {
	
	@Value("${openaq.base-url}")
	private String baseUrl;
	
	@Value("${openaq.api-key}")
	  private String apiKey;
	
	@Bean
	public WebClient openAqWebClient(WebClient.Builder builder) {
		
		return builder
				.baseUrl(baseUrl)
				.defaultHeader("X-API-Key", apiKey)
				 .build();
	}
	}


