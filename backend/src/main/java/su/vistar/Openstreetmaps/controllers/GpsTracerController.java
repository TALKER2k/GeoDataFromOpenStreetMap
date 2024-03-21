package su.vistar.Openstreetmaps.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.services.LocalPlaceGateService;
import su.vistar.Openstreetmaps.services.UsersService;

@RestController
@RequestMapping("/gps")
public class GpsTracerController {
    private final UsersService usersService;

    public GpsTracerController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/userGeoLocation")
    public void getUserGeoLocation(@RequestBody GeoLocation geoLocation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        usersService.saveUserGeoLocation(auth.getName(), geoLocation);
    }
}
