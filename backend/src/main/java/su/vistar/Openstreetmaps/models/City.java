package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Table(name = "city")
@Accessors(chain = true)
@Data
public class City {
    @Id
    @Column(name = "city_id", unique = true)
    private Long cityId;
    @Column(name = "city_name")
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "country_name")
    private Country country;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "gates_id")
    private Set<LocalPlaceGate> placeGateSet;
}
