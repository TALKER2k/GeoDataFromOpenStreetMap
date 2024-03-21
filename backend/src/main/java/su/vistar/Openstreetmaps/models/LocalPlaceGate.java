package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;

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
}

