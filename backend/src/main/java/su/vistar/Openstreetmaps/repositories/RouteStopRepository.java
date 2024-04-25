package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.RouteStop;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
}
