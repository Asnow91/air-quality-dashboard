package it.alera.airquality.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.alera.airquality.client.OpenAqClient;

@RestController
public class AirQualityController {

    private final OpenAqClient client;

    public AirQualityController(OpenAqClient client) {
        this.client = client;
    }

    @GetMapping("/api/air/now")
    public Object getAirNow() {
      long arenulaPm25 = 5079458; // <-- sensore PM2.5 giusto
      return client.latestMeasurementBySensor(arenulaPm25);
    }

    @GetMapping("/api/ping")
    public Object ping() { 
      return client.pingPm25();
    }
    
    @GetMapping("/api/air/now/compact")
    public Map<String, Object> getAirNowCompact() {
      long sensorId = 5079458;
      String json = client.latestMeasurementBySensor(sensorId);
      try {
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        var root = mapper.readTree(json).path("results");
        if (root.isArray() && root.size() > 0) {
          var r = root.get(0);
          double value = r.path("value").asDouble();
          String unit = r.path("parameter").path("units").asText();

          // semaforo
          String level;
          if (value <= 10)      level = "verde";
          else if (value <= 25) level = "giallo";
          else if (value <= 50) level = "arancione";
          else                  level = "rosso";

          return Map.of(
            "value", value,
            "unit", unit,
            "level", level
          );
        }
        return Map.of("error","no results");
      } catch (Exception e) {
        return Map.of("error","parse", "message", e.getMessage());
      }
    }



}