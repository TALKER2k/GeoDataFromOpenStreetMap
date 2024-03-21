package su.vistar.Openstreetmaps.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    @GetMapping
    public String testUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return "User login is " + authentication.getName();
    }
}

