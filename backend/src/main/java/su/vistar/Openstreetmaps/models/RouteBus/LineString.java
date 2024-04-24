package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Geometry;

@Entity
@Table(name = "line_string")
@Accessors(chain = true)
@Data
public class LineString {
    @Id
    private Long id;
    private Long routeId;
    private Geometry geom;

    @Override
    public String toString() {
        return "LineString{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", geom=" + geom +
                '}';
    }
}
