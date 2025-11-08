package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.config.StorageConfig;
import epam.gym.domain.dto.request.TraineeCreationRequest;
import epam.gym.domain.dto.request.TrainerCreationRequest;
import epam.gym.domain.dto.request.TrainingCreateRequest;
import epam.gym.domain.entities.*;
import epam.gym.application.GymFacade;
import epam.gym.infrastructure.dao.TraineeDao;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class, StorageConfig.class})
class GymFacadeFunctionsTest {
    private GymFacade gymFacade;
    private Map<String, UserDao> userStorage;
    private Map<String, TraineeDao> traineeStorage;
    private Map<String, TrainerDao> trainerStorage;

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setUserStorage(Map<String, UserDao> userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    public void setTraineeStorage(Map<String, TraineeDao> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(Map<String, TrainerDao> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @BeforeEach
    void setUp() {
        userStorage.clear();
        traineeStorage.clear();
        trainerStorage.clear();
    }
    // ============= Trainee =============

    @Test
    void testCreateTrainee() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("John");
        traineeCreationRequest.setLastName("Doe");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeCreationRequest.setAddress("123 Main St");

        Trainee createdTrainee = gymFacade.createTrainee(traineeCreationRequest);

        assertNotNull(createdTrainee);
        assertEquals("123 Main St", createdTrainee.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), createdTrainee.getDateOfBirth());
        assertNotNull(createdTrainee.getUsername());
    }

    @Test
    void testGetTrainee() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("Jane");
        traineeCreationRequest.setLastName("Smith");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        traineeCreationRequest.setAddress("456 Oak Ave");

        Trainee created = gymFacade.createTrainee(traineeCreationRequest);
        String userId = created.getUsername();

        Trainee retrieved = gymFacade.getTrainee(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUsername());
        assertEquals("456 Oak Ave", retrieved.getAddress());
    }

    @Test
    void testUpdateTrainee() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("Bob");
        traineeCreationRequest.setLastName("Johnson");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1985, 3, 10));
        traineeCreationRequest.setAddress("789 Pine Rd");

        Trainee created = gymFacade.createTrainee(traineeCreationRequest);
        String userId = created.getUsername();

        TraineeCreationRequest updateDto = new TraineeCreationRequest();
        updateDto.setFirstName("Bob");
        updateDto.setLastName("Johnson");
        updateDto.setDateOfBirth(LocalDate.of(1985, 3, 10));
        updateDto.setAddress("999 New Address");

        Trainee updated = gymFacade.updateTrainee(updateDto, userId);

        assertNotNull(updated);
        assertEquals("999 New Address", updated.getAddress());
    }

    @Test
    void testDeleteTrainee() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("Alice");
        traineeCreationRequest.setLastName("Williams");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1992, 7, 20));
        traineeCreationRequest.setAddress("321 Elm St");

        Trainee created = gymFacade.createTrainee(traineeCreationRequest);
        String userId = created.getUsername();

        gymFacade.deleteTrainee(userId);
        assertThrows(NoSuchElementException.class, () -> gymFacade.getTrainee(userId));
    }

    // ============= Trainer =============

    @Test
    void testCreateTrainer() {
        TrainerCreationRequest trainerDto = new TrainerCreationRequest();
        trainerDto.setFirstName("Mike");
        trainerDto.setLastName("Tyson");
        trainerDto.setSpecialization("Boxing");
        trainerDto.setIsActive(true);

        Trainer createdTrainer = gymFacade.createTrainer(trainerDto);

        assertNotNull(createdTrainer);
        assertEquals("Boxing", createdTrainer.getSpecialization());
        assertNotNull(createdTrainer.getUsername());
    }

    @Test
    void testGetTrainer() {
        TrainerCreationRequest trainerDto = new TrainerCreationRequest();
        trainerDto.setFirstName("Sarah");
        trainerDto.setLastName("Connor");
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(true);

        Trainer created = gymFacade.createTrainer(trainerDto);
        String userId = created.getUsername();

        Trainer retrieved = gymFacade.getTrainer(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUsername());
        assertEquals("Fitness", retrieved.getSpecialization());
    }

    @Test
    void testUpdateTrainer() {
        TrainerCreationRequest trainerCreationRequest = new TrainerCreationRequest();
        trainerCreationRequest.setFirstName("Tom");
        trainerCreationRequest.setLastName("Hardy");
        trainerCreationRequest.setSpecialization("Yoga");
        trainerCreationRequest.setIsActive(true);

        Trainer created = gymFacade.createTrainer(trainerCreationRequest);
        String userId = created.getUsername();

        TrainerCreationRequest updateRequest = new TrainerCreationRequest();
        updateRequest.setFirstName("Tom");
        updateRequest.setLastName("Hardy");
        updateRequest.setSpecialization("Pilates");
        updateRequest.setIsActive(true);

        Trainer updated = gymFacade.updateTrainer(updateRequest, userId);

        assertNotNull(updated);
        assertEquals("Pilates", updated.getSpecialization());
    }

    // ============= Training =============

    @Test
    void testCreateTraining() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("Student");
        traineeCreationRequest.setLastName("One");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeCreationRequest.setAddress("Address");
        Trainee trainee = gymFacade.createTrainee(traineeCreationRequest);

        TrainerCreationRequest trainerDto = new TrainerCreationRequest();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("One");
        trainerDto.setSpecialization("Cardio");
        trainerDto.setIsActive(true);
        Trainer trainer = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainer.getUsername());
        trainingId.setTraineeId(trainee.getUsername());
        trainingId.setTrainingName("Morning Session");

        TrainingCreateRequest trainingCreateRequest = new TrainingCreateRequest();
        trainingCreateRequest.setTrainingId(trainingId);
        trainingCreateRequest.setTrainingType("Cardio");
        trainingCreateRequest.setTrainingDate(LocalDate.now());
        trainingCreateRequest.setTrainingDuration("60");

        Training created = gymFacade.createTraining(trainingCreateRequest);

        assertNotNull(created);
        assertEquals("Cardio", created.getTrainingType());
        assertEquals("60", created.getTrainingDuration());
    }

    @Test
    void testGetTraining() {
        TraineeCreationRequest traineeCreationRequest = new TraineeCreationRequest();
        traineeCreationRequest.setFirstName("Student");
        traineeCreationRequest.setLastName("Two");
        traineeCreationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeCreationRequest.setAddress("Address");
        Trainee trainee = gymFacade.createTrainee(traineeCreationRequest);

        TrainerCreationRequest trainerDto = new TrainerCreationRequest();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("Two");
        trainerDto.setSpecialization("Strength");
        trainerDto.setIsActive(true);
        Trainer trainer = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainer.getUsername());
        trainingId.setTraineeId(trainee.getUsername());
        trainingId.setTrainingName("Evening Session");

        TrainingCreateRequest trainingCreateRequest = new TrainingCreateRequest();
        trainingCreateRequest.setTrainingId(trainingId);
        trainingCreateRequest.setTrainingType("Strength");
        trainingCreateRequest.setTrainingDate(LocalDate.now());
        trainingCreateRequest.setTrainingDuration("90");

        gymFacade.createTraining(trainingCreateRequest);

        Training retrieved = gymFacade.getTraining(trainingId);

        assertNotNull(retrieved);
        assertEquals("Strength", retrieved.getTrainingType());
        assertEquals("90", retrieved.getTrainingDuration());
    }

    @Test
    void testFacadeIsNotNull() {
        assertNotNull(gymFacade);
    }
}