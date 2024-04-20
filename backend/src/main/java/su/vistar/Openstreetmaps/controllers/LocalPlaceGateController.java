package su.vistar.Openstreetmaps.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.services.LocalPlaceGateService;

import java.util.List;

@RestController
@RequestMapping("/settings_gates")
@RequiredArgsConstructor
public class LocalPlaceGateController {
    private final LocalPlaceGateService localPlaceGateService;

    @GetMapping("/update")
    public void updateAllGates() throws InterruptedException {
        System.out.println("Method update start");
        localPlaceGateService.updateAllGates();
        System.out.println("Method update end");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllGates")
    public List<GatesDTO> getAllGates(@RequestParam("city") Long cityId) {
        System.out.println("Method getAllGates start");
        return localPlaceGateService.getAllGatesByCity(cityId);
    }
}
