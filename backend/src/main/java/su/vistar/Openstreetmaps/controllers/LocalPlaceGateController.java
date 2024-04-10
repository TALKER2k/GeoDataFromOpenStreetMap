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
    public void updateAllGates() {
        System.out.println("Method update start");
        localPlaceGateService.updateAllGates();
        System.out.println("Method update end");
    }

    @CrossOrigin(origins = "*") // Замените "http://localhost:8080" на адрес вашего фронтенд-приложения
    @GetMapping("/getAllGates")
    public List<GatesDTO> getAllGates(@RequestParam("city") String city) {
        System.out.println("Method getAllGates start");
        return localPlaceGateService.getAllGatesByCity(city);
    }
}
