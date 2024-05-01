package su.vistar.Openstreetmaps.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.models.Gates.LocalPlaceGate;
import su.vistar.Openstreetmaps.services.GateServises.LocalPlaceGateService;

import java.util.List;

@RestController
@RequestMapping("/gps")
public class GpsTrackerController {
    private final LocalPlaceGateService localPlaceGateService;

    public GpsTrackerController(LocalPlaceGateService localPlaceGateService) {
        this.localPlaceGateService = localPlaceGateService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/checkGatesAround")
    public ResponseEntity<List<LocalPlaceGate>> getUserGeoLocation(@RequestBody GeoLocation geoLocation) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok()
                .header("Server message", "Gate is opened")
                .body(localPlaceGateService.checkGatesAround(auth.getName(), geoLocation));
    }
}
