package su.vistar.Openstreetmaps.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RegistrationFormDTO {
    @NotEmpty
    @NotBlank(message = "Username name should be not empty or have spaces")
    @Size(min = 1, max = 20,  message = "out of good range")
    private String username;
    @NotEmpty
    @NotBlank(message = "Password should be not empty or have spaces")
    @Size(min = 1, max = 20, message = "out of good range")
    private String password;
}