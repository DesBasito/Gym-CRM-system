package epam.gym.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "epam.gym")
@PropertySource("classpath:application.yml")
public class ApplicationConfig {
}
