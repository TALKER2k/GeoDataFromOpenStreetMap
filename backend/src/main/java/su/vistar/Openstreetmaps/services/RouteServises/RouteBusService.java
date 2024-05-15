package su.vistar.Openstreetmaps.services.RouteServises;

import su.vistar.Openstreetmaps.DTO.RouteDTO;

import java.util.List;

public interface RouteBusService {
    List<RouteDTO> getAllRoutes();

    List<String> getPointsByRouteId(Long id);

    List<String> getWaysByRouteId(Long id);

    void updateAllBusStop();

    List<RouteDTO> getAllRoutesByCityByOSM(Long cityId);

    List<String> getWaysByRouteIdByOsm(Long id);

    List<String> getByRouteIdByOsm(Long id) throws Exception;
    List<String> getLinesByRouteIdByOsm(Long id) throws Exception;
}
