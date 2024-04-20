package su.vistar.Openstreetmaps.services.impl;

import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.Country;
import su.vistar.Openstreetmaps.repositories.CityRepository;
import su.vistar.Openstreetmaps.repositories.CountryRepository;
import su.vistar.Openstreetmaps.services.DropdownService;

import java.util.List;

@Service
public class DropdownServiceImpl implements DropdownService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;

    public DropdownServiceImpl(CityRepository cityRepository, CountryRepository countryRepository) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
    }

    @Override
    public List<Country> getAllCountry() {
        return countryRepository.findAll();
    }

    @Override
    public List<City> getCityByCountry(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Country is not found"));
        return cityRepository.findByCountry(country);
    }
}
