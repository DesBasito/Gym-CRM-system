package epam.gym.infrastructure.repositories;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.specifications.TrainingSpecification;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
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
        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        epam.gym.constants.TrainingType tType = epam.gym.constants.TrainingType.valueOf("FITNESS");
        TrainingType type = trainingTypeRepository.findTrainingTypeByTrainingTypeName(tType).orElse(null);

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
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "Alice.Brown",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                null,
                "FITNESS"
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTraineeTrainings(filterRequest));

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        trainings.forEach(t -> {
            assertEquals(filterRequest.getUsername(), t.getTrainee().getUser().getUsername());
            assertTrue(t.getTrainingDate().isAfter(filterRequest.getPeriodFrom().minusDays(1)) &&
                      t.getTrainingDate().isBefore(filterRequest.getPeriodTo().plusDays(1)));
        });
    }

    @Test
    void testFindTraineeTrainings_withOnlyUsername_shouldReturnAllTrainings() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "Alice.Brown", null, null, null, null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTraineeTrainings(filterRequest));

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 2);
        trainings.forEach(t -> assertEquals(filterRequest.getUsername(), t.getTrainee().getUser().getUsername()));
    }

    @Test
    void testFindTraineeTrainings_withDateRange_shouldFilterByDate() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "Alice.Brown",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 16),
                null,
                null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTraineeTrainings(filterRequest));

        assertNotNull(trainings);
        trainings.forEach(t -> {
            assertTrue(t.getTrainingDate().isAfter(filterRequest.getPeriodFrom().minusDays(1)));
            assertTrue(t.getTrainingDate().isBefore(filterRequest.getPeriodTo().plusDays(1)));
        });
    }

    @Test
    void testFindTraineeTrainings_withNonExistentTrainee_shouldReturnEmptyList() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "NonExistent.Trainee", null, null, null, null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTraineeTrainings(filterRequest));

        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
    }

    @Test
    void testFindTrainerTrainings_withAllFilters_shouldReturnMatchingTrainings() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "John.Doe",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 31),
                null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTrainerTrainings(filterRequest));

        assertNotNull(trainings);
        assertFalse(trainings.isEmpty());
        trainings.forEach(t -> {
            assertEquals(filterRequest.getUsername(), t.getTrainer().getUser().getUsername());
            assertTrue(t.getTrainingDate().isAfter(filterRequest.getPeriodFrom().minusDays(1)) &&
                      t.getTrainingDate().isBefore(filterRequest.getPeriodTo().plusDays(1)));
        });
    }

    @Test
    void testFindTrainerTrainings_withOnlyUsername_shouldReturnAllTrainings() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "John.Doe", null, null, null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTrainerTrainings(filterRequest));

        assertNotNull(trainings);
        assertTrue(trainings.size() >= 2);
        trainings.forEach(t -> assertEquals(filterRequest.getUsername(), t.getTrainer().getUser().getUsername()));
    }

    @Test
    void testFindTrainerTrainings_withDateRange_shouldFilterByDate() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "John.Doe",
                LocalDate.of(2024, 1, 15),
                LocalDate.of(2024, 1, 16),
                null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTrainerTrainings(filterRequest));

        assertNotNull(trainings);
        trainings.forEach(t -> {
            assertTrue(t.getTrainingDate().isAfter(filterRequest.getPeriodFrom().minusDays(1)));
            assertTrue(t.getTrainingDate().isBefore(filterRequest.getPeriodTo().plusDays(1)));
        });
    }

    @Test
    void testFindTrainerTrainings_withNonExistentTrainer_shouldReturnEmptyList() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "NonExistent.Trainer", null, null, null
        );

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTrainerTrainings(filterRequest));

        assertNotNull(trainings);
        assertTrue(trainings.isEmpty());
    }

    @Test
    void testSave_updateExistingTraining_shouldUpdate() {
        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        epam.gym.constants.TrainingType tType = epam.gym.constants.TrainingType.valueOf("FITNESS");
        TrainingType type = trainingTypeRepository.findTrainingTypeByTrainingTypeName(tType).orElse(null);

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
