package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Entity
@Table(name = "country")
@Accessors(chain = true)
@Data
public class Country {
    @Id
    @Column(name = "country_id", unique = true)
    private Long countryId;
    @Column(name = "country_name")
    private String name;
    @Column(name = "ISO3166-1")
    private String abbreviation;
    @Column(name = "ISO3166_1_alpha2")
    private String abbreviationAlpha2;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "city_name")
    private Set<City> cities;
}
