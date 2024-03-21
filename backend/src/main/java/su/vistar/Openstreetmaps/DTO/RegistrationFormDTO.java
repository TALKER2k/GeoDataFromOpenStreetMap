package su.vistar.Openstreetmaps.DTO;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegistrationFormDTO {
    private String username;
    private String password;
}