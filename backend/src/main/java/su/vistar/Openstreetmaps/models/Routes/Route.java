package su.vistar.Openstreetmaps.models.Routes;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Table(name = "route", schema = "route_osm")
@Accessors(chain = true)
@Data
public class Route {
    @Id
    private Long id;
    private String name;
    @Column(name = "from_location")
    private String from;
    @Column(name = "to_location")
    private String to;
    private String network;
    private String operator;
    @Column(name = "public_transport_version")
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
