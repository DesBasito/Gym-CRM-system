package epam.gym.domain.services;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TrainingServiceIntegrationTest {
    private final TrainingService trainingService;
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final EntityManager entityManager;

    @Autowired
    public TrainingServiceIntegrationTest(EntityManager entityManager, TrainerRepository trainerRepository, TraineeRepository traineeRepository, TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.entityManager = entityManager;
        this.trainerRepository = trainerRepository;
        this.traineeRepository = traineeRepository;
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    private String traineeUsername;
    private String trainerUsername;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.flush();

        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("123 Main St");
        RegistrationResponse traineeResponse = traineeService.create(traineeRequest);
        traineeUsername = traineeResponse.getUsername();

        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("FITNESS");
        RegistrationResponse trainerResponse = trainerService.create(trainerRequest);
        trainerUsername = trainerResponse.getUsername();

        entityManager.flush();
    }

    @Test
    void testCreateTraining_shouldUpdateTrainersTraineesRelationship() {
        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        TrainingModel createdTraining = trainingService.create(trainingRequest);
        entityManager.flush();

        assertNotNull(createdTraining);
        assertEquals("Morning Workout", createdTraining.getTrainingName());

        Trainee trainee = traineeRepository.findByUser_Username(traineeUsername).orElse(null);
        Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElse(null);

        assertNotNull(trainee, "Trainee should exist");
        assertNotNull(trainer, "Trainer should exist");

        assertTrue(trainee.getTrainers().contains(trainer),
                "Trainee should have trainer in their trainers list");
        assertTrue(trainer.getTrainees().contains(trainee),
                "Trainer should have trainee in their trainees list");

        assertEquals(1, trainee.getTrainers().size(),
                "Trainee should have exactly 1 trainer");
        assertEquals(1, trainer.getTrainees().size(),
                "Trainer should have exactly 1 trainee");
    }

    @Test
    void testCreateMultipleTrainings_shouldNotDuplicateRelationship() {
        TrainingRequest firstRequest = new TrainingRequest();
        firstRequest.setTraineeUsername(traineeUsername);
        firstRequest.setTrainerUsername(trainerUsername);
        firstRequest.setTrainingName("Morning Workout");
        firstRequest.setTrainingType("FITNESS");
        firstRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        firstRequest.setTrainingDuration(60);

        TrainingRequest secondRequest = new TrainingRequest();
        secondRequest.setTraineeUsername(traineeUsername);
        secondRequest.setTrainerUsername(trainerUsername);
        secondRequest.setTrainingName("Evening Workout");
        secondRequest.setTrainingType("FITNESS");
        secondRequest.setTrainingDate(LocalDate.of(2024, 1, 16));
        secondRequest.setTrainingDuration(45);

        trainingService.create(firstRequest);
        entityManager.flush();
        entityManager.clear();

        trainingService.create(secondRequest);
        entityManager.flush();
        entityManager.clear();

        Trainee trainee = traineeRepository.findByUser_Username(traineeUsername).orElseThrow();
        Trainer trainer = trainerRepository.findByUser_Username(trainerUsername).orElseThrow();

        assertEquals(1, trainee.getTrainers().size(),
                "Trainee should still have exactly 1 trainer (no duplicates)");
        assertEquals(1, trainer.getTrainees().size(),
                "Trainer should still have exactly 1 trainee (no duplicates)");

        assertEquals(2, trainee.getTrainings().size(),
                "Trainee should have 2 trainings");
        assertEquals(2, trainer.getTrainings().size(),
                "Trainer should have 2 trainings");
    }

    @Test
    void testDeleteTrainer_shouldCascadeDeleteTrainingsButNotTrainee() {
        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        trainingService.create(trainingRequest);
        entityManager.flush();

        Trainee traineeBefore = traineeRepository.findByUser_Username(traineeUsername).orElseThrow();
        assertEquals(1, traineeBefore.getTrainers().size(), "Trainee should have 1 trainer before deletion");
        assertEquals(1, traineeBefore.getTrainings().size(), "Trainee should have 1 training before deletion");

        trainerService.delete(trainerUsername);
        entityManager.flush();

        Trainee traineeAfter = traineeRepository.findByUser_Username(traineeUsername).orElse(null);
        assertNotNull(traineeAfter, "Trainee should still exist after trainer deletion");
        assertEquals(0, traineeAfter.getTrainers().size(),
                "Trainee's trainers list should be empty after trainer deletion");
        assertEquals(0, traineeAfter.getTrainings().size(),
                "Trainee's trainings should be cascade deleted when trainer is deleted (DB level)");

        assertTrue(trainerRepository.findByUser_Username(trainerUsername).isEmpty(),
                "Trainer should be deleted");
    }

    @Test
    void testDeleteTrainee_shouldCascadeDeleteTrainingsButNotTrainer() {
        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        trainingService.create(trainingRequest);
        entityManager.flush();

        Trainer trainerBefore = trainerRepository.findByUser_Username(trainerUsername).orElseThrow();
        assertEquals(1, trainerBefore.getTrainees().size(), "Trainer should have 1 trainee before deletion");
        assertEquals(1, trainerBefore.getTrainings().size(), "Trainer should have 1 training before deletion");

        traineeService.delete(traineeUsername);
        entityManager.flush();

        Trainer trainerAfter = trainerRepository.findByUser_Username(trainerUsername).orElse(null);
        assertNotNull(trainerAfter, "Trainer should still exist after trainee deletion");
        assertEquals(0, trainerAfter.getTrainees().size(),
                "Trainer's trainees list should be empty after trainee deletion");
        assertEquals(0, trainerAfter.getTrainings().size(),
                "Trainer's trainings should be cascade deleted when trainee is deleted (DB level)");

        assertTrue(traineeRepository.findByUser_Username(traineeUsername).isEmpty(),
                "Trainee should be deleted");
    }
}
