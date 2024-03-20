package su.vistar.Openstreetmaps.services;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import su.vistar.Openstreetmaps.DTO.RegistrationFormDTO;
import su.vistar.Openstreetmaps.models.Employee;
import su.vistar.Openstreetmaps.models.Role;
import su.vistar.Openstreetmaps.repositories.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    public UsersService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public Employee registerUser(RegistrationFormDTO registrationFormDTO) {
        Employee employeeFromDB = new Employee()
                .setUsername(registrationFormDTO.getUsername())
                .setPassword(bCryptPasswordEncoder.encode(registrationFormDTO.getPassword()))
                .setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));

        userRepository.save(employeeFromDB);

        return employeeFromDB;
    }

    public boolean existsByUserName(String username) {
        return userRepository.existsByUsername(username);
    }
}
