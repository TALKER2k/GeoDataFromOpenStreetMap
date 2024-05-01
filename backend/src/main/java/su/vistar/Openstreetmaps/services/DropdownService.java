package su.vistar.Openstreetmaps.services;

import su.vistar.Openstreetmaps.models.Gates.City;
import su.vistar.Openstreetmaps.models.Gates.Country;

import java.util.List;

public interface DropdownService {
    List<Country> getAllCountry();
    List<City> getCityByCountry(Long id);
}
