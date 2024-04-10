package su.vistar.Openstreetmaps.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;
import su.vistar.Openstreetmaps.repositories.LocalPlaceGateRepository;
import su.vistar.Openstreetmaps.repositories.UserRepository;
import su.vistar.Openstreetmaps.services.LocalPlaceGateService;
import su.vistar.Openstreetmaps.services.TelephoneService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class LocalPlaceGateServiceImpl implements LocalPlaceGateService {
    private final LocalPlaceGateRepository localPlaceGateRepository;
    private final TelephoneService telephoneService;
    private final UserRepository userRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    @Scheduled(cron = "0 0 20 * * *")
    public void updateAllGatesAutomaticaly() {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        //примерно центр Воронежа
        double latitudeCurrentNode = 51.661535;
        double longitudeCurrentNode = 39.200287;
        //14 км радиуса, чтоб охватить весь Воронеж
        int radiusMeters = 15000;
        List<String> barriersType = new ArrayList<>();
        barriersType.add("lift_gate");
        barriersType.add("gate");
        for (String barrier : barriersType) {
            String query = "[out:json];" +
                    "(node[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "way[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "relation[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + "););" +
                    "out;";
            try {
                String response = sendOverpassQuery(overpassUrl, query);
                processOverpassResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateAllGates() {
        String overpassUrl = "https://overpass-api.de/api/interpreter";
        //примерно центр Воронежа
        double latitudeCurrentNode = 51.661535;
        double longitudeCurrentNode = 39.200287;
        //14 км радиуса, чтоб охватить весь Воронеж
        int radiusMeters = 15000;
        List<String> barriersType = new ArrayList<>();
        barriersType.add("lift_gate");
        barriersType.add("gate");
        for (String barrier : barriersType) {
            String query = "[out:json];" +
                    "(node[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "way[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + ");" +
                    "relation[barrier=" + barrier + "](around:" + radiusMeters + "," +
                    latitudeCurrentNode + "," + longitudeCurrentNode + "););" +
                    "out;";
            try {
                String response = sendOverpassQuery(overpassUrl, query);
                processOverpassResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<LocalPlaceGate> checkGatesAround(String username, GeoLocation geoLocation) {
        executor.submit(() -> {

            Employee employee = userRepository.findByUsername(username);
            employee.setLatitude(geoLocation.latitude())
                    .setLongitude(geoLocation.longitude());

            userRepository.save(employee);

            List<LocalPlaceGate> placeGateList = localPlaceGateRepository.findNearbyAmbulanceVehicles(
                    employee.getLatitude(),
                    employee.getLongitude(),
                    0.05
            );

            if (!placeGateList.isEmpty()) {
                for (LocalPlaceGate gate : placeGateList) {
                    telephoneService.callByNumber(gate.getPhoneNumber());
                }
            }

            return placeGateList;

        });
        return null;
    }

    @Override
    public List<GatesDTO> getAllGatesByCity(String city) {
        List<LocalPlaceGate> gatesList = localPlaceGateRepository.findAll()
                .stream()
                .limit(50)
                .toList();
        List<GatesDTO> gatesDTOList = new ArrayList<>();

        for (LocalPlaceGate gates : gatesList) {
            GatesDTO dto = new GatesDTO(gates.getLongitude(), gates.getLatitude());
            gatesDTOList.add(dto);
        }
        System.out.println(gatesDTOList);
        return gatesDTOList;
    }

    @SneakyThrows(JSONException.class)
    private void processOverpassResponse(String response) {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            if(!element.has("lat")) {
                continue;
            }

            double lat = element.getDouble("lat");
            double lon = element.getDouble("lon");

            LocalPlaceGate localPlaceGate = new LocalPlaceGate();
            localPlaceGate.setGatesId(id);
            localPlaceGate.setLatitude(lon);
            localPlaceGate.setLongitude(lat);
            localPlaceGate.setPhoneNumber("+79001234567");
            localPlaceGate.setUpdate_date(LocalDateTime.now());

            JSONObject tags = element.getJSONObject("tags");
            if (tags.has("barrier")) {
                localPlaceGate.setName(tags.getString("barrier"));
            }

            localPlaceGateRepository.save(localPlaceGate);
        }
    }

    private String sendOverpassQuery(String overpassUrl, String query) throws Exception {
        URL url = new URL(overpassUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        connection.getOutputStream().write(("data=" + query).getBytes("UTF-8"));

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

