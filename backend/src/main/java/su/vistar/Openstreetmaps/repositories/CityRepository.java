package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import su.vistar.Openstreetmaps.models.City;

public interface CityRepository extends JpaRepository<City, Long> {
}
