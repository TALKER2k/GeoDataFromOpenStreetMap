package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

@Entity
@Table(name = "point")
@Accessors(chain = true)
@Data
public class Point {
    @Id
    private UUID id;
    private Long stopId;
    private Geometry geom;
}
