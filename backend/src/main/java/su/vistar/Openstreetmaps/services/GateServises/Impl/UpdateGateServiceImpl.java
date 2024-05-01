package su.vistar.Openstreetmaps.services.GateServises.Impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.models.Gates.City;
import su.vistar.Openstreetmaps.models.Gates.Country;
import su.vistar.Openstreetmaps.models.Gates.LocalPlaceGate;
import su.vistar.Openstreetmaps.repositories.GateRepositories.CityRepository;
import su.vistar.Openstreetmaps.repositories.GateRepositories.CountryRepository;
import su.vistar.Openstreetmaps.repositories.GateRepositories.LocalPlaceGateRepository;
import su.vistar.Openstreetmaps.services.GateServises.UpdateGateService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UpdateGateServiceImpl implements UpdateGateService {
    private final LocalPlaceGateRepository localPlaceGateRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final ExecutorService executorForCity = Executors.newFixedThreadPool(10);
    private final ExecutorService executorForGate = Executors.newFixedThreadPool(10);
    static String overpassUrl = "https://overpass-api.de/api/interpreter";
    @Scheduled(cron = "* 0/34 22 * * *")
    @Override
    public void updateAllGates() throws InterruptedException {
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
                    executorForCity.submit(() -> {
                        try {
                            Random random = new Random();
                            Thread.sleep(500 + random.nextInt(1001));
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

        Thread.sleep(300000); //5 min for sleep


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
                    String finalQuery = query;
                    executorForGate.submit(() ->
                    {
                        try {
                            Random random = new Random();
                            Thread.sleep(random.nextInt(1500));
                            String response = sendOverpassQuery(overpassUrl, finalQuery);
                            processOverpassResponseForGate(response, city);
                            System.out.println(count.incrementAndGet() + "/" + cityList.size());
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
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


    public static String sendOverpassQuery(String overpassUrl, String query) throws Exception {
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
