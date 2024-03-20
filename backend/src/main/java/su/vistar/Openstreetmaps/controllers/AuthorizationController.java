package su.vistar.Openstreetmaps.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.LoginDTO;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.services.UsersService;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final UsersService userService;

    public AuthorizationController(UsersService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Employee> registerEmployee(@RequestBody RegistrationFormDTO registrationFormDto) {

        if (userService.existsByUserName(registrationFormDto.getUsername())) {
            return ResponseEntity.badRequest()
                    .header("Server message", "Username is taken")
                    .build();
        }

        Employee employee = userService.registerUser(registrationFormDto);

        return ResponseEntity.ok()
                .header("Server message", "User registered successfully")
                .body(employee);
    }
}
