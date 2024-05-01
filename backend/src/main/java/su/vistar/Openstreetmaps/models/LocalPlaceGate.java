package su.vistar.Openstreetmaps.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "local_places_lift_gates", schema = "lift_gate_osm")
@Getter
@Setter
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
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "city_id")
    private City city;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalPlaceGate that = (LocalPlaceGate) o;
        return Objects.equals(gatesId, that.gatesId) && Objects.equals(longitude, that.longitude) && Objects.equals(latitude, that.latitude) && Objects.equals(name, that.name) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(update_date, that.update_date) && Objects.equals(cityLocation, that.cityLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gatesId, longitude, latitude, name, phoneNumber, update_date, cityLocation);
    }
}

