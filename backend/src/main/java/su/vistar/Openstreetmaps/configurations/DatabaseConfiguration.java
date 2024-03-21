package su.vistar.Openstreetmaps.configurations;


import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan("su.vistar.Openstreetmaps.models")
@EnableJpaRepositories("su.vistar.Openstreetmaps.repositories")
@EnableTransactionManagement
public class DatabaseConfiguration {
}
