package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.Point;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
