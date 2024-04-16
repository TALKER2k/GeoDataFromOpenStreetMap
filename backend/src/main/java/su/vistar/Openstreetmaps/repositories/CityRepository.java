package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su.vistar.Openstreetmaps.models.City;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;

public interface CityRepository extends JpaRepository<City, Long> {
    @Query(value = "SELECT * from city where city_name = :nameCity", nativeQuery = true)
    City findIdCity(@Param("nameCity") String city);
}
