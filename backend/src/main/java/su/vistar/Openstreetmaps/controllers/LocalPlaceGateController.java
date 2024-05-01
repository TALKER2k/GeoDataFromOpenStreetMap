package su.vistar.Openstreetmaps.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.GatesDTO;
import su.vistar.Openstreetmaps.services.LocalPlaceGateService;

import java.util.List;

@RestController
@RequestMapping("/settings_gates")
@RequiredArgsConstructor
@Slf4j
public class LocalPlaceGateController {
    private final LocalPlaceGateService localPlaceGateService;

    @CrossOrigin(origins = "*")
    @GetMapping("/update")
    public void updateAllGates() throws InterruptedException {
        log.info("Method update start");
        localPlaceGateService.updateAllGates();
        log.info("Method update end");
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllGatesByDB")
    public List<GatesDTO> getAllGates(@RequestParam("city") Long cityId) {
        log.info("Method getAllGatesByDB start");
        return localPlaceGateService.getAllGatesByCity(cityId);
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/getAllGatesByOSM")
    public List<GatesDTO> getAllGatesByOSM(@RequestParam("city") Long cityId) {
        log.info("Method getAllGatesByOSM start");
        return localPlaceGateService.getAllGatesByCityByOSM(cityId);
    }



}
