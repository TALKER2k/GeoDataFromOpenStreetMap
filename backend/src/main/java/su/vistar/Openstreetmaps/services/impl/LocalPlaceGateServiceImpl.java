package su.vistar.Openstreetmaps.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.Country;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;
import su.vistar.Openstreetmaps.repositories.CountryRepository;
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
    private final CountryRepository countryRepository;
//    private final ModelMapper modelMapper;

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
                processOverpassResponseForCountry(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void updateAllGates() {
        String overpassUrl = "https://overpass-api.de/api/interpreter";

        new Thread(() -> {
            String query = "[out:json];" +
                    "area[\"ISO3166-1\"~\".*\"][admin_level=2];\n" +
                    "(node[\"place\"=\"country\"](area););\n" +
                    "out;";
            try {
                String response = sendOverpassQuery(overpassUrl, query);
                processOverpassResponseForCountry(response);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }).start();

//        List<String> queries = new ArrayList<>();
//        queries.add(query);
//        query = "[out:json];" +
//                "area[\"name\"=\"Portugal\"][admin_level=2];\n" +
//                "(node[\"place\"=\"city\"](area););\n" +
//                "out;";
//        queries.add(query);
//        for (String q : queries) {
//            try {
//                String response = sendOverpassQuery(overpassUrl, q);
//                processOverpassResponseForCountry(response);
//            } catch (Exception e) {
//                e.printStackTrace();
//
//            }
//        }
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
        List<LocalPlaceGate> gatesList = localPlaceGateRepository.findAll();
        List<GatesDTO> gatesDTOList = new ArrayList<>();

        for (LocalPlaceGate gates : gatesList) {
//            modelMapper.map(gates, GatesDTO.class);
            GatesDTO dto = new GatesDTO(gates.getLongitude(), gates.getLatitude(),
                    gates.getName(), gates.getPhoneNumber());
            gatesDTOList.add(dto);
        }
        System.out.println(gatesDTOList);
        return gatesDTOList;
    }

    @SneakyThrows(JSONException.class)
    private void processOverpassResponseForCountry(String response) {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            System.out.println(element);
            long id = element.getLong("id");

            Country country = new Country();
            country.setCountryId(id);

            JSONObject tags = element.getJSONObject("tags");
            if (tags.has("name:en")) {
                country.setName(tags.getString("name:en"));
            }
            if (tags.has("ISO3166-1")) {
                country.setAbbreviation(tags.getString("ISO3166-1"));
            }
            if (tags.has("ISO3166-1:alpha2")) {
                country.setAbbreviationAlpha2(tags.getString("ISO3166-1:alpha2"));
            }

            countryRepository.save(country);
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

