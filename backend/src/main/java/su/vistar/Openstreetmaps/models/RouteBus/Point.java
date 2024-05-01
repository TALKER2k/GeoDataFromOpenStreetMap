package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "point", schema = "route_osm")
@Accessors(chain = true)
@Data
public class Point {
    @Id
    private UUID id;
    private Long stopId;
    private Long routeId;
    @Column(columnDefinition = "geometry(Point,4326)")
    private org.locationtech.jts.geom.Point point;
}
