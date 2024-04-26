package su.vistar.Openstreetmaps.controllers;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.models.RouteBus.LineString;
import su.vistar.Openstreetmaps.models.RouteBus.Route;
import su.vistar.Openstreetmaps.services.impl.RouteBusService;

import java.util.List;

@RestController
@RequestMapping("/route")
public class RouteBusController {
    private final RouteBusService routeBusService;

    public RouteBusController(RouteBusService routeBusService) {
        this.routeBusService = routeBusService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/updateBDRouteBus")
    public void updateBDRouteBus() {
        System.out.println("updateBDRouteBus start...");
        routeBusService.updateAllBusStop();
        System.out.println("updateBDRouteBus end...");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllRoutes")
    public List<Route> getRoutes() {
        System.out.println("getAllRoutes start...");
        return routeBusService.getAllRoutes();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("getLinesByRouteId/{id}")
    public List<Coordinate[]> getLinesByRouteId(@PathVariable("id") Long id) {
        return routeBusService.getWaysByRouteId(id);
    }
}
