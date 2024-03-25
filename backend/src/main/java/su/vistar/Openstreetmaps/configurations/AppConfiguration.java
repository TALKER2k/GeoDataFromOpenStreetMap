package su.vistar.Openstreetmaps.configurations;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {
        "su.vistar.Openstreetmaps.controllers",
        "su.vistar.Openstreetmaps.services",
        "su.vistar.Openstreetmaps.models"
})
@EnableAspectJAutoProxy
public class AppConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
