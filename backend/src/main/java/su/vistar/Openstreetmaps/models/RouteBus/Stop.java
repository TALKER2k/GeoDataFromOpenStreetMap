package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Table(name = "stop", schema = "route_osm")
@Accessors(chain = true)
@Data
public class Stop {
    @Id
    private Long id;
    private String name;
    private double lat;
    private double lon;

    @OneToMany(mappedBy = "stop", cascade = CascadeType.ALL)
    private Set<RouteStop> routeStops;

    @Override
    public String toString() {
        return "Stop{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                '}';
    }
}
