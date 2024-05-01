package su.vistar.Openstreetmaps.services;

import su.vistar.Openstreetmaps.DTO.RouteDTO;

import java.util.List;

public interface RouteBusService {
    List<RouteDTO> getAllRoutes();

    List<String> getPointsByRouteId(Long id);

    List<String> getWaysByRouteId(Long id);

    void updateAllBusStop();
}
