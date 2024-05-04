package su.vistar.Openstreetmaps.configurations;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
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
@RequiredArgsConstructor
public class AppConfiguration {
    private final GeographyProperties geographyProperties;
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory(
                new PrecisionModel(),
                geographyProperties.getSrid()
        );
    }

}
