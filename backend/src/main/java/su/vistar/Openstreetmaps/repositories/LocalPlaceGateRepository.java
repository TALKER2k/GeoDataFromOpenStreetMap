package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocalPlaceGateRepository extends JpaRepository<LocalPlaceGate, Long> {
    Optional<LocalPlaceGate> findByLatitudeAndLongitude(Double latitude, Double longitude);

    //формула расчета расстояния между двумя точками на поверхности Земли
    @Query(value = "SELECT * FROM local_place_gate " +
            "WHERE " +
            "6371 * 2 * ASIN(SQRT(POW(SIN((RADIANS(latitude) - RADIANS(:gateLatitude)) / 2), 2) + " +
            "COS(RADIANS(:gateLatitude)) * COS(RADIANS(latitude)) * " +
            "POW(SIN((RADIANS(longitude) - RADIANS(:gateLongitude)) / 2), 2))) <= :radiusInKm", nativeQuery = true)
    List<LocalPlaceGate> findNearbyAmbulanceVehicles(
            @Param("gateLatitude") double gateLatitude,
            @Param("gateLongitude") double gateLongitude,
            @Param("radiusInKm") double radiusInKm
    );
    @Query(value = "SELECT * from local_places_lift_gates where city_id=:cityId", nativeQuery = true)
    List<LocalPlaceGate> findGateByIdCity(@Param("cityId") long id);
}
