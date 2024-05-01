package su.vistar.Openstreetmaps.services;

import su.vistar.Openstreetmaps.DTO.LoginDTO;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;

public interface UserService {
    Employee registerUser(RegistrationFormDTO registrationFormDto);

    String loginUser(LoginDTO loginDto);

    boolean existsByUserName(String username);
}
