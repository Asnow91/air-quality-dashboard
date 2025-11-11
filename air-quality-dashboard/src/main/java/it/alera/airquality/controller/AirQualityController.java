package it.alera.airquality.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import it.alera.airquality.client.OpenAqClient;

@RestController
public class AirQualityController {

	@Value("${monitor.pm25.sensor-ids}")
	private String sensorIdsCsv;
	
	@Value("${monitor.pm25.names}")
	private String namesCsv;
	
    private final OpenAqClient client;

    public AirQualityController(OpenAqClient client) {
        this.client = client;
    }

    @GetMapping("/api/air/now")
    public Object getAirNow() {
      long villaGuglielmiPm25 = 21829;
      return client.latestMeasurementBySensor(villaGuglielmiPm25);
    }

    @GetMapping("/api/ping")
    public Object ping() { 
      return client.pingPm25();
    }
    

    @GetMapping("/api/air/now/compact/all")
    public Object getAllPm25Compact() {
      String[] ids = sensorIdsCsv.split(",");
      String[] names = namesCsv.split(",");
      var out = new java.util.ArrayList<java.util.Map<String,Object>>();

      for (int i = 0; i < ids.length; i++) {
        long sensorId = Long.parseLong(ids[i].trim());
        String json = client.latestMeasurementBySensor(sensorId);
        try {
          var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
          var root = mapper.readTree(json).path("results");
          if (root.isArray() && root.size() > 0) {
            var r = root.get(0);
            double value = r.path("value").asDouble();
            String unit = r.path("parameter").path("units").asText();

            String level;
            if (value <= 10)      level = "verde";
            else if (value <= 25) level = "giallo";
            else if (value <= 50) level = "arancione";
            else                  level = "rosso";

            out.add(Map.of(
              "name",  names[i].trim(),
              "sensorId", sensorId,
              "value", value,
              "unit",  unit,
              "level", level
            ));
          } else {
            out.add(Map.of("name", names[i].trim(), "sensorId", sensorId, "error", "no results"));
          }
        } catch (Exception e) {
          out.add(Map.of("name", names[i].trim(), "sensorId", sensorId, "error", "parse"));
        }
      }
      return out;
    }


    @GetMapping("/api/air/near-rome")
    public Object nearRome() {
      return client.listLocationsNearRomePm25();
    }



}