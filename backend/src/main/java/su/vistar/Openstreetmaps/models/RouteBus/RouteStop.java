package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Table(name = "route_stop")
@Accessors(chain = true)
@Data
public class RouteStop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
