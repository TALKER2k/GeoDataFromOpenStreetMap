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
    @Query("SELECT a FROM LocalPlaceGate a " +
            "WHERE " +
            "6371 * 2 * ASIN(SQRT(POW(SIN((RADIANS(a.latitude) - RADIANS(:gateLatitude)) / 2), 2) + " +
            "COS(RADIANS(:gateLatitude)) * COS(RADIANS(a.latitude)) * " +
            "POW(SIN((RADIANS(a.longitude) - RADIANS(:gateLongitude)) / 2), 2))) <= :radiusInKm")
    List<LocalPlaceGate> findNearbyAmbulanceVehicles(
            @Param("gateLatitude") double gateLatitude,
            @Param("gateLongitude") double gateLongitude,
            @Param("radiusInKm") double radiusInKm
    );
}
