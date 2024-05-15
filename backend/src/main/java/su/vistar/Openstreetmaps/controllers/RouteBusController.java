package su.vistar.Openstreetmaps.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.RouteDTO;
import su.vistar.Openstreetmaps.services.RouteServises.RouteBusService;

import java.util.List;

@RestController
@RequestMapping("/route")
@Slf4j
public class RouteBusController {
    private final RouteBusService routeBusService;
    public RouteBusController(RouteBusService routeBusService) {
        this.routeBusService = routeBusService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/updateBDRouteBus")
    public void updateBDRouteBus() {
        log.info("updateBDRouteBus start...");
        routeBusService.updateAllBusStop();
        log.info("updateBDRouteBus end...");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllRoutes")
    public List<RouteDTO> getRoutes() {
       log.info("getAllRoutes start...");
        return routeBusService.getAllRoutes();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getLinesByRouteId/{id}")
    public List<String> getLinesByRouteId(@PathVariable("id") Long id) {
        log.info("getAllRoutes start... Id = " + id);
        return routeBusService.getWaysByRouteId(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getPointsByRouteId/{id}")
    public List<String> getPointsByRouteId(@PathVariable("id") Long id) {
        log.info("getAllRoutes start... Id = " + id);
        return routeBusService.getPointsByRouteId(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllRouteByOSM/{id}")
    public List<RouteDTO> getAllGatesByOSM(@PathVariable("id") Long cityId) {
        log.info("Method getAllGatesByOSM start");
        return routeBusService.getAllRoutesByCityByOSM(cityId);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getLinesByRouteIdByOsm/{id}")
    public List<String> getLinesByRouteIdByOsm(@PathVariable("id") Long id) {
        log.info("getAllRoutes start... Id = " + id);
        return routeBusService.getWaysByRouteIdByOsm(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getPointsByRouteIdByOsm/{id}")
    public List<String> getPointsByRouteIdByOsm(@PathVariable("id") Long id)
            throws Exception {
        log.info("getRoute start... Id = " + id);
        return routeBusService.getByRouteIdByOsm(id);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getLineByRouteIdByOsm/{id}")
    public List<String> getLineByRouteIdByOsm(@PathVariable("id") Long id)
            throws Exception {
        log.info("getRoute start... Id = " + id);
        return routeBusService.getLinesByRouteIdByOsm(id);
    }
}
