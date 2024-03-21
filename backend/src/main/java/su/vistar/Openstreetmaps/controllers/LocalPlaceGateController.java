package su.vistar.Openstreetmaps.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.vistar.Openstreetmaps.services.LocalPlaceGateService;

@RestController
@RequestMapping("/settings_gates")
@RequiredArgsConstructor
public class LocalPlaceGateController {
    private final LocalPlaceGateService localPlaceGateService;

    @GetMapping("/update")
    public void updateAllGates() {
        localPlaceGateService.updateAllGates();
    }
}
