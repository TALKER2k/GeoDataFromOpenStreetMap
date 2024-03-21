package su.vistar.Openstreetmaps.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import su.vistar.Openstreetmaps.models.Employee;

@Repository
public interface UserRepository extends JpaRepository<Employee, Long> {
    Employee findByUsername(String username);
    Boolean existsByUsername(String username);
}
