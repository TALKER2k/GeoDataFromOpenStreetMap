package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.LineString;

import java.util.List;

@Repository
public interface LineStringRepository extends JpaRepository<LineString, Long> {
    @Query("SELECT ST_AsGeoJSON(s.geom) FROM LineString s WHERE s.routeId = :route_id")
    List<String> findByRouteId(@Param("route_id") Long id);
}
