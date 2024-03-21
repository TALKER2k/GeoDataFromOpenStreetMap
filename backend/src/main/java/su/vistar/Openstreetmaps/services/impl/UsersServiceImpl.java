package su.vistar.Openstreetmaps.services.impl;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.vistar.Openstreetmaps.DTO.GeoLocation;
import su.vistar.Openstreetmaps.DTO.LoginDTO;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.LocalPlaceGate;
import su.vistar.Openstreetmaps.models.Role;
import su.vistar.Openstreetmaps.repositories.LocalPlaceGateRepository;
import su.vistar.Openstreetmaps.repositories.RoleRepository;
import su.vistar.Openstreetmaps.repositories.UserRepository;
import su.vistar.Openstreetmaps.security.JWTGenerator;
import su.vistar.Openstreetmaps.security.SecurityConstants;
import su.vistar.Openstreetmaps.services.TelephoneService;
import su.vistar.Openstreetmaps.services.UserService;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class UsersServiceImpl implements UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;

    @Override
    @Transactional
    public Employee registerUser(RegistrationFormDTO registrationFormDto) {
        Role roles = roleRepository.findByName(SecurityConstants.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        Employee registeredUser = new Employee()
                .setUsername(registrationFormDto.getUsername())
                .setPassword(passwordEncoder.encode((registrationFormDto.getPassword())))
                .setRoles(Collections.singleton(roles)) ;
        userRepository.save(registeredUser);

        return registeredUser;
    }


    @Override
    public String loginUser(LoginDTO loginDto) {
        log.info("Authentication. User {}", loginDto.username());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.username(),
                        loginDto.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtGenerator.generateToken(authentication);
    }

    @Override
    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }
}
