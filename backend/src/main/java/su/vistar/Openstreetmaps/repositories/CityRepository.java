package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.Country;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {
    City findByName(String city);
    List<City> findByCountryOrderByName(Country country);
}
