package su.vistar.Openstreetmaps.repositories.GateRepositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.Gates.Country;

import java.util.List;
@Repository

public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findAllByOrderByName();
}
