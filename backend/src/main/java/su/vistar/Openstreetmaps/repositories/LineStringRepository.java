package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.RouteBus.LineString;

import java.util.List;

@Repository
public interface LineStringRepository extends JpaRepository<LineString, Long> {
    public List<LineString> findByRouteId(Long id);
}
