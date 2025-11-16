package epam.gym.infrastructure.repositories;

import epam.gym.config.TestConfig;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import jakarta.persistence.EntityManager;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
class TrainingRepositoryTest {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final Flyway flyway;

    @Autowired
    public TrainingRepositoryTest(TrainingRepository trainingRepository, TraineeRepository traineeRepository, TrainerRepository trainerRepository, TrainingTypeRepository trainingTypeRepository,  Flyway flyway) {
        this.trainingRepository = trainingRepository;
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
        this.trainingTypeRepository = trainingTypeRepository;
        this.flyway = flyway;
    }

    @BeforeEach
    void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void testSave_newTraining_shouldPersist() {
        Trainee trainee = traineeRepository.findByUsername("Alice.Brown");
        Trainer trainer = trainerRepository.findByUsername("John.Doe");
        TrainingType type = trainingTypeRepository.findByName("FITNESS");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Test Training");
        training.setTrainingType(type);
        training.setTrainingDate(LocalDate.of(2024, 2, 1));
        training.setTrainingDuration(60);

        Training saved = trainingRepository.save(training);

        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertEquals("Test Training", saved.getTrainingName());
    }

    @Test
    void testFindTraineeTrainings_withAllFilters_shouldReturnMatchingTrainings() {
        String traineeUsername = "Alice.Brown";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);
        String trainingType = "FITNESS";

        List<Training> trainings = trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        trainings.forEach(t -> {
            assertEquals(traineeUsername, t.getTrainee().getUser().getUsername());
            assertTrue(t.getTrainingDate().isAfter(fromDate.minusDays(1)) && t.getTrainingDate().isBefore(toDate.plusDays(1)));
        });
    }

    @Test
    void testFindTraineeTrainings_withOnlyUsername_shouldReturnAllTrainings() {
        String traineeUsername = "Alice.Brown";

        List<Training> trainings = trainingRepository.findTraineeTrainings(traineeUsername, null, null, null);

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 2);
        trainings.forEach(t -> assertEquals(traineeUsername, t.getTrainee().getUser().getUsername()));
    }

    @Test
    void testFindTraineeTrainings_withDateRange_shouldFilterByDate() {
        String traineeUsername = "Alice.Brown";
        LocalDate fromDate = LocalDate.of(2024, 1, 15);
        LocalDate toDate = LocalDate.of(2024, 1, 16);

        List<Training> trainings = trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, null);

        assertNotNull(trainings);
        trainings.forEach(t -> {
            assertTrue(t.getTrainingDate().isAfter(fromDate.minusDays(1)));
            assertTrue(t.getTrainingDate().isBefore(toDate.plusDays(1)));
        });
    }

    @Test
    void testFindTraineeTrainings_withNonExistentTrainee_shouldReturnEmptyList() {
        String traineeUsername = "NonExistent.Trainee";

        List<Training> trainings = trainingRepository.findTraineeTrainings(traineeUsername, null, null, null);

        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
    }

    @Test
    void testFindTrainerTrainings_withAllFilters_shouldReturnMatchingTrainings() {
        String trainerUsername = "John.Doe";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 1, 31);

        List<Training> trainings = trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate);

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        trainings.forEach(t -> {
            assertEquals(trainerUsername, t.getTrainer().getUser().getUsername());
            assertTrue(t.getTrainingDate().isAfter(fromDate.minusDays(1)) && t.getTrainingDate().isBefore(toDate.plusDays(1)));
        });
    }

    @Test
    void testFindTrainerTrainings_withOnlyUsername_shouldReturnAllTrainings() {
        String trainerUsername = "John.Doe";

        List<Training> trainings = trainingRepository.findTrainerTrainings(trainerUsername, null, null);

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 2);
        trainings.forEach(t -> assertEquals(trainerUsername, t.getTrainer().getUser().getUsername()));
    }

    @Test
    void testFindTrainerTrainings_withDateRange_shouldFilterByDate() {
        String trainerUsername = "John.Doe";
        LocalDate fromDate = LocalDate.of(2024, 1, 15);
        LocalDate toDate = LocalDate.of(2024, 1, 16);

        List<Training> trainings = trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate);

        assertNotNull(trainings);
        trainings.forEach(t -> {
            assertTrue(t.getTrainingDate().isAfter(fromDate.minusDays(1)));
            assertTrue(t.getTrainingDate().isBefore(toDate.plusDays(1)));
        });
    }

    @Test
    void testFindTrainerTrainings_withNonExistentTrainer_shouldReturnEmptyList() {
        String trainerUsername = "NonExistent.Trainer";

        List<Training> trainings = trainingRepository.findTrainerTrainings(trainerUsername, null, null);

        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
    }

    @Test
    void testSave_updateExistingTraining_shouldUpdate() {
        Trainee trainee = traineeRepository.findByUsername("Alice.Brown");
        Trainer trainer = trainerRepository.findByUsername("John.Doe");
        TrainingType type = trainingTypeRepository.findByName("FITNESS");

        Training training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Original Name");
        training.setTrainingType(type);
        training.setTrainingDate(LocalDate.of(2024, 2, 1));
        training.setTrainingDuration(60);

        Training saved = trainingRepository.save(training);
        Long id = saved.getId();

        saved.setTrainingName("Updated Name");
        saved.setTrainingDuration(90);
        Training updated = trainingRepository.save(saved);

        assertEquals(id, updated.getId());
        assertEquals("Updated Name", updated.getTrainingName());
        assertEquals(90, updated.getTrainingDuration());
    }
}
