package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_places_bus_stop")
@Data
public class LocalPlaceBusStop {
    @Id
    @Column(name = "bus_stop_id", unique = true)
    private Long gatesId;
    @Column(name = "lon")
    private Double longitude;
    @Column(name = "lat")
    private Double latitude;
    @Column(name = "name")
    private String name;
}