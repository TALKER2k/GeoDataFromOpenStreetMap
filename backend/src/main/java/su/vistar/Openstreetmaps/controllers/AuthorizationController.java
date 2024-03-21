package su.vistar.Openstreetmaps.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.AuthorizationResponseDTO;
import su.vistar.Openstreetmaps.DTO.LoginDTO;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.services.UsersService;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final UsersService usersService;

    public AuthorizationController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthorizationResponseDTO> login(@RequestBody LoginDTO loginDto) {

        String token = usersService.loginUser(loginDto);

        return ResponseEntity.ok()
                .header("Server message", "Login succeeded")
                .body(new AuthorizationResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody RegistrationFormDTO registrationFormDto) {

        if (usersService.existsByUserName(registrationFormDto.getUsername())) {
            return ResponseEntity.badRequest()
                    .header("Server message", "Username is taken")
                    .build();
        }

        Employee employee = usersService.registerUser(registrationFormDto);

        return ResponseEntity.ok()
                .header("Server message", "User registered successfully")
                .body(employee);
    }

}
