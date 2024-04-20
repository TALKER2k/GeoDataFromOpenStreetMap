package su.vistar.Openstreetmaps.services;

import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;

import java.util.List;

public interface LocalPlaceGateService {
    void updateAllGates() throws InterruptedException;

    List<LocalPlaceGate> checkGatesAround(String username, GeoLocation geoLocation);
    List<GatesDTO> getAllGatesByCity(Long city);
}
