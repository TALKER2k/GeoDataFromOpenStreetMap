package su.vistar.Openstreetmaps.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.Country;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;
import su.vistar.Openstreetmaps.repositories.CityRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LocalPlaceGateServiceImpl implements LocalPlaceGateService {
    private final LocalPlaceGateRepository localPlaceGateRepository;
    private final TelephoneService telephoneService;
    private final UserRepository userRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
//    private final ModelMapper modelMapper;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);
    private final ExecutorService executor2 = Executors.newFixedThreadPool(9);
    private final ExecutorService executor3 = Executors.newFixedThreadPool(4);

    @Scheduled(cron = "0 0 19 * * *")
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
    public void updateAllGates() throws InterruptedException {
        String overpassUrl = "https://overpass-api.de/api/interpreter";

        Thread countryUpdateDB = new Thread(() -> {
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
        });
        countryUpdateDB.start();
        try {
            countryUpdateDB.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread cityUpdateDB = new Thread(() -> {
            List<Country> countryList = countryRepository.findAll();
            AtomicInteger count = new AtomicInteger();
            for (Country country : countryList) {
                String nameCountry;
                String query = null;
                if (country.getName() != null) {
                    nameCountry = country.getName();
                    query = "[out:json];" +
                            "area[\"name\"=\"" + nameCountry + "\"][admin_level=2];\n" +
                            "(node[\"place\"=\"city\"](area););\n" +
                            "out;";
                }

                if (country.getAbbreviationAlpha2() != null) {
                    nameCountry = country.getAbbreviationAlpha2();
                    query = "[out:json];" +
                            "area[\"ISO3166-1\"=\"" + nameCountry + "\"][admin_level=2];\n" +
                            "(node[\"place\"=\"city\"](area););\n" +
                            "out;";
                }

                if (country.getAbbreviation() != null) {
                    nameCountry = country.getAbbreviation();
                    query = "[out:json];" +
                            "area[\"ISO3166-1\"=\"" + nameCountry + "\"][admin_level=2];\n" +
                            "(node[\"place\"=\"city\"](area););\n" +
                            "out;";
                }
                if (query == null) {
                    continue;
                }

                try {
                    String finalQuery = query;
                    executor2.submit(() -> {
                        try {
                            String response = sendOverpassQuery(overpassUrl, finalQuery);
                            processOverpassResponseForCity(response, country);
                            System.out.println(count.incrementAndGet() + "/" + countryList.size());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        cityUpdateDB.start();
        try {
            cityUpdateDB.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread.sleep(10000);


        Thread gateUpdateDB = new Thread(() -> {
            List<City> cityList = cityRepository.findAll();
            AtomicInteger count = new AtomicInteger();
            for (City city : cityList) {
                String query = null;
                if (city.getName() != null) {
                    query = "[out:json];" +
                            "area[\"name\"=\"" + city.getName() + "\"];\n" +
                            "(node[barrier=lift_gate](area););\n" +
                            "out;";
                }

                if (query == null) {
                    continue;
                }

                try {
                    executor3.submit(() -> {
                    });
                    try {
                        String response = sendOverpassQuery(overpassUrl, query);
                        processOverpassResponseForGate(response, city);
                        System.out.println(count.incrementAndGet() + "/" + cityList.size());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        gateUpdateDB.start();
        try {
            gateUpdateDB.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows({JSONException.class, ConstraintViolationException.class})
    private void processOverpassResponseForGate(String response, City city) {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");
        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            if (!element.has("lat")) {
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
            localPlaceGate.setCity(city);

            Set<LocalPlaceGate> gates = city.getGates();
            if (gates == null) {
                gates = new HashSet<>();
                city.setGates(gates);
            }

            gates.add(localPlaceGate);
            city.setGates(gates);

            JSONObject tags = element.getJSONObject("tags");
            if (tags.has("barrier")) {
                localPlaceGate.setName(tags.getString("barrier"));
            }

            localPlaceGateRepository.save(localPlaceGate);
        }
    }


    @SneakyThrows({JSONException.class, ConstraintViolationException.class})
    private void processOverpassResponseForCity(String response, Country country) {
        JSONObject jsonResponse = new JSONObject(response);

        JSONArray elements = jsonResponse.getJSONArray("elements");

        List<City> citiesToAdd = new ArrayList<>(); // Создаем список для добавления всех городов

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            City city = new City();
            city.setCityId(id);

            JSONObject tags = element.optJSONObject("tags"); // Используем optJSONObject для того, чтобы избежать NullPointerException
            if (tags != null && tags.has("name")) {
                city.setName(tags.getString("name"));
            }

            city.setCountry(country);
            citiesToAdd.add(city);
        }

        Set<City> cities = country.getCities();
        if (cities == null) {
            cities = new HashSet<>();
        }
        cities.addAll(citiesToAdd);
        country.setCities(cities);

        cityRepository.saveAll(citiesToAdd);
        countryRepository.save(country);
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

        System.out.println("Method Country start...");
        JSONArray elements = jsonResponse.getJSONArray("elements");

        List<Country> countriesToAdd = new ArrayList<>(); // Создаем список для добавления всех стран

        for (int i = 0; i < elements.length(); i++) {
            JSONObject element = elements.getJSONObject(i);
            long id = element.getLong("id");

            Country country = new Country();
            country.setCountryId(id);

            JSONObject tags = element.optJSONObject("tags"); // Используем optJSONObject для того, чтобы избежать NullPointerException
            if (tags != null) {
                if (tags.has("name:en")) {
                    country.setName(tags.getString("name:en"));
                }
                if (tags.has("ISO3166-1")) {
                    country.setAbbreviation(tags.getString("ISO3166-1"));
                }
                if (tags.has("ISO3166-1:alpha2")) {
                    country.setAbbreviationAlpha2(tags.getString("ISO3166-1:alpha2"));
                }
                if (tags.has("country_code_iso3166_1_alpha_2")) {
                    country.setAbbreviationAlpha2(tags.getString("country_code_iso3166_1_alpha_2"));
                }
            }

            countriesToAdd.add(country);
        }

        countryRepository.saveAll(countriesToAdd);

        System.out.println("Method Country end.");
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

