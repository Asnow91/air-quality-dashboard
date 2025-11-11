package it.alera.airquality.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;

@Configuration
public class WebClientConfig {
  private static final Logger log = LoggerFactory.getLogger(WebClientConfig.class);

  @Value("${openaq.base-url}") private String baseUrl;
  @Value("${openaq.api-key}")  private String apiKey;

  @Bean
  public WebClient openAqWebClient(WebClient.Builder builder) {
    return builder.baseUrl(baseUrl).defaultHeader("X-API-Key", apiKey).build();
  }

  @PostConstruct
  void debugConfig() {
    String masked = (apiKey == null || apiKey.isBlank())
        ? "MISSING"
        : (apiKey.length() <= 6 ? "***" : apiKey.substring(0,3) + "..." + apiKey.substring(apiKey.length()-3));
    log.info("[OpenAQ] baseUrl='{}', apiKeyPresent={}, apiKey(masked)={}",
        baseUrl, (apiKey != null && !apiKey.isBlank()), masked);
  }
}
