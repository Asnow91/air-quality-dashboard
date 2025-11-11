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

public String listLocationsNearRomePm25() {

	String latLon = "41.7287,12.2789";

 System.out.println("[OpenAQ] GET /locations?coordinates=" + latLon + "&radius=12000&parameters_id=2&limit=5&country=IT");

 return webClient.get()
     .uri(b -> b.path("/locations")
                .queryParam("coordinates", latLon)   // lat,lon (NON invertire)
                .queryParam("radius", 12000)         // in metri
                .queryParam("parameters_id", 2)      // PM2.5
                .queryParam("iso", "IT")         // filtra Italia
                .queryParam("limit", 5)
                .build())
     .retrieve()
     .onStatus(s -> s.is4xxClientError() || s.is5xxServerError(),
               resp -> resp.bodyToMono(String.class)
                           .map(msg -> new RuntimeException("OpenAQ error " + resp.statusCode() + ": " + msg)))
     .bodyToMono(String.class)
     .block();
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


}
