package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.LocalPlaceBusStop;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;

@Repository
public interface LocalPlaceBusStopRepository extends JpaRepository<LocalPlaceBusStop, Long> {

}
