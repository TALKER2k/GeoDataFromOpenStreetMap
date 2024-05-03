package su.vistar.Openstreetmaps.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import su.vistar.Openstreetmaps.DTO.AuthorizationResponseDTO;
import su.vistar.Openstreetmaps.DTO.LoginDTO;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.services.impl.UsersServiceImpl;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    private final UsersServiceImpl usersService;

    public AuthorizationController(UsersServiceImpl usersService) {
        this.usersService = usersService;
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/login")
    public ResponseEntity<AuthorizationResponseDTO> login(@RequestBody LoginDTO loginDto) {

        String token = usersService.loginUser(loginDto);

        return ResponseEntity.ok()
                .header("Server message", "Login succeeded")
                .body(new AuthorizationResponseDTO(token));
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/register")
    public ResponseEntity<Employee> register(@RequestBody RegistrationFormDTO registrationFormDto) {
        System.out.println(111);
        if (usersService.existsByUserName(registrationFormDto.getUsername())) {
            return ResponseEntity.badRequest()
                    .header("Server message", "Username is taken")
                    .build();
        }
        System.out.println(222);

        Employee employee = usersService.registerUser(registrationFormDto);

        System.out.println(333);
        return ResponseEntity.ok()
                .header("Server message", "User registered successfully")
                .body(employee);
    }
}
