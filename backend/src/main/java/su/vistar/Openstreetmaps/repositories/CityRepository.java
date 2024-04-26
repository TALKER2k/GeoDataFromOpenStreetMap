package su.vistar.Openstreetmaps.repositories;

import liquibase.change.DatabaseChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.Country;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    City findByName(String city);
    List<City> findByCountryOrderByName(Country country);
}
