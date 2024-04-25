package su.vistar.Openstreetmaps.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/updateBDRouteBus")
    public void updateBDRouteBus() {
        System.out.println("updateBDRouteBus start...");
        routeBusService.updateAllBusStop();
        System.out.println("updateBDRouteBus end...");
    }

    @GetMapping("/getAllRoutes")
    public List<Route> getRoutes() {
        System.out.println("updateBDRouteBus start...");
        return routeBusService.getAllRoutes();
    }
}
