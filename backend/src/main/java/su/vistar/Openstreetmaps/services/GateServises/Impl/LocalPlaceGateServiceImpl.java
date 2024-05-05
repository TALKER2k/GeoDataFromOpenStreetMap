package su.vistar.Openstreetmaps.services.GateServises.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.Gates.City;
import su.vistar.Openstreetmaps.models.Gates.LocalPlaceGate;
import su.vistar.Openstreetmaps.repositories.GateRepositories.CityRepository;
import su.vistar.Openstreetmaps.repositories.GateRepositories.LocalPlaceGateRepository;
import su.vistar.Openstreetmaps.repositories.UserRepository;
import su.vistar.Openstreetmaps.services.GateServises.LocalPlaceGateService;
import su.vistar.Openstreetmaps.services.TelephoneService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static su.vistar.Openstreetmaps.services.GateServises.Impl.UpdateGateServiceImpl.sendOverpassQuery;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocalPlaceGateServiceImpl implements LocalPlaceGateService {
    private final LocalPlaceGateRepository localPlaceGateRepository;
    private final TelephoneService telephoneService;
    private final UserRepository userRepository;
    private final CityRepository cityRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

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
            log.info("Method checkGatesAround end");
            return placeGateList;

        });
        log.info("Method checkGatesAround end");
        return null;
    }

    @Override
    public List<GatesDTO> getAllGatesByCity(Long cityId){
        City city = cityRepository.findById(cityId).orElseThrow(() -> new RuntimeException("not fount city"));
        List<GatesDTO> gatesDTOList = new ArrayList<>();

        List<LocalPlaceGate> gatesList = localPlaceGateRepository.findGateByIdCity(city.getCityId());

        for (LocalPlaceGate gates : gatesList) {
            gatesDTOList.add(new GatesDTO(gates.getLongitude(),gates.getLatitude(),gates.getName(),gates.getPhoneNumber()));
        }

        System.out.println(gatesDTOList);
        log.info("Method getAllGatesByCity end");
        return gatesDTOList;
    }

    @Override
    public  List<GatesDTO> getAllGatesByCityByOSM(Long cityId){
        City city = cityRepository.findById(cityId).orElseThrow(() -> new RuntimeException("not fount city"));
        String cityName = city.getName();
        String overpassUrl = "https://overpass-api.de/api/interpreter";

        String query = "[out:json];" +
                "area[\"name\"=\"" + cityName + "\"];\n" +
                "(node[barrier=lift_gate](area););\n" +
                "out;";
        List<GatesDTO> resultGates = new ArrayList<>();
        try {
            String response = sendOverpassQuery(overpassUrl, query);
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray elements = jsonResponse.getJSONArray("elements");
            for (int i = 0;i < elements.length();i++){
                JSONObject element = elements.getJSONObject(i);
                double lat = element.getDouble("lat");
                double lon = element.getDouble("lon");
                String name = "lift_gates";
                String phoneNumber = "+7987654321";
                resultGates.add(new GatesDTO(lat,lon,name,phoneNumber));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("Method getAllGatesByCityByOSM end");
        return resultGates;
    }

}

