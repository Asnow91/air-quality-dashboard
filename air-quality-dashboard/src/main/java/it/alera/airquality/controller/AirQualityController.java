package it.alera.airquality.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.alera.airquality.client.OpenAqClient;

@RestController
public class AirQualityController {

	@Value("${monitor.stations.ids}")
	private String stationIdsCsv;

	@Value("${monitor.stations.names}")
	private String stationNamesCsv;
	
	@Value("${monitor.pm25.sensor-ids}")
	private String pm25IdsCsv;
	
	@Value("${monitor.pm10.sensor-ids}")
	private String pm10IdsCsv;

    private final OpenAqClient client;

    public AirQualityController(OpenAqClient client) {
        this.client = client;
    }

//    @GetMapping("/api/air/now")
//    public Object getAirNow() {
//      long villaGuglielmiPm25 = 21829;
//      return client.latestMeasurementBySensor(villaGuglielmiPm25);
//    }

    @GetMapping("/api/ping")
    public Object ping() { 
      return client.pingPm25();
    }
    

    @GetMapping("/api/air/now/all")
    public Object getAllStations() {
        String[] locIds = stationIdsCsv.split(",");
        String[] names = stationNamesCsv.split(",");
        String[] pm25Ids = pm25IdsCsv.split(",");
        String[] pm10Ids = pm10IdsCsv.split(",");

        var out = new ArrayList<Map<String,Object>>();
        var mapper = new ObjectMapper();

        for (int i = 0; i < locIds.length; i++) {

            long locationId = Long.parseLong(locIds[i].trim());
            long pm25Sensor = Long.parseLong(pm25Ids[i].trim());
            long pm10Sensor = Long.parseLong(pm10Ids[i].trim());
            String name = names[i].trim();

            var json = client.latestByLocation(locationId);

            Double pm25 = null;
            Double pm10 = null;

            try {
                var results = mapper.readTree(json).path("results");

                for (var node : results) {
                    long sid = node.path("sensorsId").asLong();
                    double value = node.path("value").asDouble();
                    if (sid == pm25Sensor) pm25 = value;
                    if (sid == pm10Sensor) pm10 = value;
                }

                String level =
                        (pm25 == null) ? "n/d" :
                        (pm25 <= 10) ? "verde" :
                        (pm25 <= 25) ? "giallo" :
                        (pm25 <= 50) ? "arancione" :
                        "rosso";

                out.add(Map.of(
                    "stazione", name,
                    "locationId", locationId,
                    "pm25", pm25,
                    "pm10", pm10,
                    "livello", level
                ));

            } catch(Exception e) {
                out.add(Map.of("stazione", name, "error", "parse"));
            }
        }

        return out;
    }


  

    @GetMapping("/api/debug/location/{id}")
    public String debugLocationLatest(@org.springframework.web.bind.annotation.PathVariable long id) {
        return client.latestByLocation(id);
    }


}