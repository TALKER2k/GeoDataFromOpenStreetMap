package su.vistar.Openstreetmaps.models.Gates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "country", schema = "lift_gate_osm")
@Accessors(chain = true)
@Getter
@Setter
public class Country {
    @Id
    @Column(name = "country_id", unique = true)
    private Long countryId;
    @Column(name = "country_name")
    @NotNull
    private String name;
    @Column(name = "ISO3166_1")
    private String abbreviation;
    @Column(name = "ISO3166_1_alpha2")
    private String abbreviationAlpha2;
    @OneToMany(mappedBy = "country", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<City> cities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(countryId, country.countryId) && Objects.equals(name, country.name) && Objects.equals(abbreviation, country.abbreviation) && Objects.equals(abbreviationAlpha2, country.abbreviationAlpha2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countryId, name, abbreviation, abbreviationAlpha2);
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryId=" + countryId +
                ", name='" + name + '\'' +
                ", abbreviation='" + abbreviation + '\'' +
                ", abbreviationAlpha2='" + abbreviationAlpha2 + '\'' +
                ", cities=" + cities.size() +
                '}';
    }
}
