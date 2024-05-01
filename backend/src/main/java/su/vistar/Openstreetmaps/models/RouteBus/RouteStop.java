package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "route_stop", schema = "route_osm")
@Accessors(chain = true)
@Data
public class RouteStop {
    @Id
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne
    @JoinColumn(name = "stop_id")
    private Stop stop;
    private Integer sequence;

    @Override
    public String toString() {
        return "RouteStop{" +
                "id=" + id +
                ", routeId=" + route.getId() +
                ", stopId=" + stop.getId() +
                ", sequence=" + sequence +
                '}';
    }
}
