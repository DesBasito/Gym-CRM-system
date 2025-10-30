package epam.gym.config;

import epam.gym.domain.entities.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {
    @Bean
    public Map<String, User> userStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<EmbeddedTrainingId, Training> trainingStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, TrainingType> trainingTypeStorage() {
        return new HashMap<>();
    }
}
