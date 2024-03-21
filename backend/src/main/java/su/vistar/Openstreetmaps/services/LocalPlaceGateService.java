package su.vistar.Openstreetmaps.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.repositories.LocalPlaceGateRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
public class LocalPlaceGateService {
    private final LocalPlaceGateRepository localPlaceGateRepository;

    @Scheduled(fixedDelay = 10000)
    public void calculateCoordinatesForCurrentUser() {
        try {
            String geoLocationData = sendRequest();

            JsonObject jsonObject = JsonParser.parseString(geoLocationData).getAsJsonObject();

            String city = jsonObject.get("city").getAsString();
            String region = jsonObject.get("region").getAsString();
            String country = jsonObject.get("country").getAsString();
            double latitude = jsonObject.get("latitude").getAsDouble();
            double longitude = jsonObject.get("longitude").getAsDouble();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String sendRequest() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://ipapi.co/json/").openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }
}
