package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import su.vistar.Openstreetmaps.models.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
}
