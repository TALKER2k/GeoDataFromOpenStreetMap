package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "city")
@Accessors(chain = true)
@Getter
@Setter
public class City {
    @Id
    @Column(name = "city_id", unique = true)
    private Long cityId;
    @Column(name = "city_name")
    private String name;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;
    @OneToMany(mappedBy = "city", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<LocalPlaceGate> gates;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(cityId, city.cityId) && Objects.equals(name, city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityId, name);
    }
}
