package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.locationtech.jts.geom.Geometry;

import java.util.UUID;

@Entity
@Table(name = "line_string", schema = "route_osm")
@Accessors(chain = true)
@Data
public class LineString {
    @Id
    private UUID idLine;
    private Long id;
    private Long routeId;
    @Column(columnDefinition = "geometry(LineString,4326)")
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
