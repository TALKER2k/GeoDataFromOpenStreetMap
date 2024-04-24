package su.vistar.Openstreetmaps.models.RouteBus;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Table(name = "route")
@Accessors(chain = true)
@Data
public class Route {
    @Id
    private Long id;
    private String name;
    private String from;
    private String to;
    private String network;
    private String operator;
    @Column(name = "public_transport:version")
    private Integer publicTransport;
    private String ref;
    private String route;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL)
    private Set<RouteStop> routeStops;

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", network='" + network + '\'' +
                ", operator='" + operator + '\'' +
                ", publicTransport=" + publicTransport +
                ", ref='" + ref + '\'' +
                ", route='" + route + '\'' +
                '}';
    }
}
