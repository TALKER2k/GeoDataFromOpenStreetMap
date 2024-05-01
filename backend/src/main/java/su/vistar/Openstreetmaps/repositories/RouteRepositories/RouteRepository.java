package su.vistar.Openstreetmaps.repositories.RouteRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.Routes.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
