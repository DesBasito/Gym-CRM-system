package epam.gym.config;

import epam.gym.infrastructure.entities.*;
import epam.gym.infrastructure.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

@DataJpaTest
@ActiveProfiles("test")
public abstract class AbstractRepositoryTest {

    @Autowired
    protected TestEntityManager entityManager;

    @Autowired
    protected TraineeRepository traineeRepository;

    @Autowired
    protected TrainerRepository trainerRepository;

    @Autowired
    protected TrainingTypeRepository trainingTypeRepository;

    @Autowired
    protected TrainingRepository trainingRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @BeforeEach
    public void setupTestData() {
        Role traineeRole = roleRepository.findByName("ROLE_TRAINEE").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_TRAINEE");
            return roleRepository.save(role);
        });

        Role trainerRole = roleRepository.findByName("ROLE_TRAINER").orElseGet(() -> {
            Role role = new Role();
            role.setName("ROLE_TRAINER");
            return roleRepository.save(role);
        });

        epam.gym.infrastructure.entities.TrainingType fitnessType =
            trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.FITNESS)
                .orElseGet(() -> {
                    epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                    type.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);
                    return trainingTypeRepository.save(type);
                });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.YOGA)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.YOGA);
                return trainingTypeRepository.save(type);
            });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.CARDIO)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.CARDIO);
                return trainingTypeRepository.save(type);
            });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.BOXING)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.BOXING);
                return trainingTypeRepository.save(type);
            });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.PILATES)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.PILATES);
                return trainingTypeRepository.save(type);
            });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.CROSSFIT)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.CROSSFIT);
                return trainingTypeRepository.save(type);
            });

        trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.SWIMMING)
            .orElseGet(() -> {
                epam.gym.infrastructure.entities.TrainingType type = new epam.gym.infrastructure.entities.TrainingType();
                type.setTrainingTypeName(epam.gym.constants.TrainingType.SWIMMING);
                return trainingTypeRepository.save(type);
            });

        User traineeUser = userRepository.findByUsername("Alice.Brown").orElseGet(() -> {
            User user = new User();
            user.setFirstName("Alice");
            user.setLastName("Brown");
            user.setUsername("Alice.Brown");
            user.setPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
            user.setIsActive(true);
            user.setEnabled(true);
            user.getRoles().add(traineeRole);
            return userRepository.save(user);
        });

        User trainerUser = userRepository.findByUsername("John.Doe").orElseGet(() -> {
            User user = new User();
            user.setFirstName("John");
            user.setLastName("Doe");
            user.setUsername("John.Doe");
            user.setPassword("$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG");
            user.setIsActive(true);
            user.setEnabled(true);
            user.getRoles().add(trainerRole);
            return userRepository.save(user);
        });

        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseGet(() -> {
            Trainee t = new Trainee();
            t.setUser(traineeUser);
            t.setDateOfBirth(LocalDate.of(1995, 5, 15));
            t.setAddress("123 Main St");
            return traineeRepository.save(t);
        });

        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseGet(() -> {
            Trainer t = new Trainer();
            t.setUser(trainerUser);
            t.setSpecialization(fitnessType);
            return trainerRepository.save(t);
        });

        if (trainingRepository.count() == 0) {
            Training training1 = new Training();
            training1.setTrainee(trainee);
            training1.setTrainer(trainer);
            training1.setTrainingName("Morning Workout");
            training1.setTrainingType(fitnessType);
            training1.setTrainingDate(LocalDate.of(2024, 1, 15));
            training1.setTrainingDuration(60);
            trainingRepository.save(training1);

            Training training2 = new Training();
            training2.setTrainee(trainee);
            training2.setTrainer(trainer);
            training2.setTrainingName("Evening Session");
            training2.setTrainingType(fitnessType);
            training2.setTrainingDate(LocalDate.of(2024, 1, 20));
            training2.setTrainingDuration(45);
            trainingRepository.save(training2);
        }
    }
}
