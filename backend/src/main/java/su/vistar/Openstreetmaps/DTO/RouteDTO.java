package su.vistar.Openstreetmaps.DTO;

import lombok.Data;

@Data
public class RouteDTO{
        Long id;
        String name;
        String from;
        String ref;

        public RouteDTO(Long id, String name, String from, String ref) {
                this.id = id;
                this.name = name;
                this.from = from;
                this.ref = ref;
        }

        public RouteDTO() {
        }
}

