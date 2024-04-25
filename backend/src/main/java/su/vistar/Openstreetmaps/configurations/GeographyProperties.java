package su.vistar.Openstreetmaps.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix="geography")
public class GeographyProperties {
    private int srid;
}

