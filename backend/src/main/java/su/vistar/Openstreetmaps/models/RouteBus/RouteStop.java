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
    private Long routeId;
    private Long stopId;
    private Integer sequence;

    @Override
    public String toString() {
        return "RouteStop{" +
                "id=" + id +
                ", routeId=" + routeId +
                ", stopId=" + stopId +
                ", sequence=" + sequence +
                '}';
    }
}
