package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "local_places_lift_gates")
@Data
public class LocalPlaceGate {
    @Id
    @Column(name = "gates_id", unique = true)
    private Long gatesId;
    @Column(name = "lon")
    private Double longitude;
    @Column(name = "lat")
    private Double latitude;
    @Column(name = "name")
    private String name;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "update_date")
    private LocalDateTime update_date;
    @Column(name = "city_location")
    private String cityLocation;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "city_name")
    private City city;
}

