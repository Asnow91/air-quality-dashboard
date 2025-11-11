// it.alera.airquality.client.OpenAqClient
package it.alera.airquality.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAqClient {
	
  private final WebClient webClient;

  public OpenAqClient(@Qualifier("openAqWebClient") WebClient webClient) {
	    this.webClient = webClient;
	  }




  public String pingPm25() {
	  return webClient.get().uri(b -> b.path("/parameters/2").build())
	      .retrieve().bodyToMono(String.class).block();
	}



  public String latestMeasurementBySensor(long sensorId) {
	  String from = java.time.Instant.now().minus(java.time.Duration.ofDays(7)).toString(); // 7 giorni fa (UTC)
	  return webClient.get()
	      .uri(b -> b.path("/sensors/{id}/measurements")
	                 .queryParam("datetime_from", from)
	                 .queryParam("limit", 1)
	                 .build(sensorId))
	      .retrieve()
	      .bodyToMono(String.class)
	      .block();
	}

  public String latestByLocation(long locationId) {
	  return webClient.get()
	      .uri(b -> b.path("/locations/{id}/latest").build(locationId))
	      .retrieve()
	      .bodyToMono(String.class)
	      .block();
	}


}
