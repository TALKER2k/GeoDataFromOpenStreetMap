package su.vistar.Openstreetmaps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StarterApp {
    public static void main(String[] args) {
        SpringApplication.run(StarterApp.class, args);
    }
}
