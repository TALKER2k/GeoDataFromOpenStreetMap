package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.Point;

import java.util.List;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    @Query("SELECT ST_AsGeoJSON(p.point) FROM Point p WHERE p.routeId = :route_id")
    List<String> findByRouteId(@Param("route_id") Long id);
}
