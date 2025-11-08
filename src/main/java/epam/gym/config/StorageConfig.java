package epam.gym.config;

import epam.gym.infrastructure.dao.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {
    @Bean
    public Map<String, UserDao> userStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, TrainerDao> trainerStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, TraineeDao> traineeStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<EmbeddedTrainingDaoId, TrainingDao> trainingStorage() {
        return new HashMap<>();
    }

    @Bean
    public Map<String, TrainingTypeDao> trainingTypeStorage() {
        return new HashMap<>();
    }
}
