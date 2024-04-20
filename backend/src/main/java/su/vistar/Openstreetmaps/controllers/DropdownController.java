package su.vistar.Openstreetmaps.controllers;

import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.Country;
import su.vistar.Openstreetmaps.services.DropdownService;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class DropdownController {
    private final DropdownService dropdownService;

    public DropdownController(DropdownService dropdownService) {
        this.dropdownService = dropdownService;
    }

    @CrossOrigin(origins = "*")
    @GetMapping
    public List<Country> getCountryFront() {
        return dropdownService.getAllCountry();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/{selectedCountry}/cities")
    public List<City> getCityByCountryId(@PathVariable(value = "selectedCountry") Long countryId) {
        return dropdownService.getCityByCountry(countryId);
    }

}
