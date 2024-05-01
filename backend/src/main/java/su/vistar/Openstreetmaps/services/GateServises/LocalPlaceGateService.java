package su.vistar.Openstreetmaps.services.GateServises;

import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.Gates.LocalPlaceGate;

import java.util.List;

public interface LocalPlaceGateService {

    List<LocalPlaceGate> checkGatesAround(String username, GeoLocation geoLocation);
    List<GatesDTO> getAllGatesByCity(Long city);

    List<GatesDTO> getAllGatesByCityByOSM(Long cityId);
}
