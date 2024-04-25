package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
