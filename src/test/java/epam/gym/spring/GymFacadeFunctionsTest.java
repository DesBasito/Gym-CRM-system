package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.*;
import epam.gym.application.GymFacade;
import epam.gym.infrastructure.entities.UserDao;
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
    private Map<String, TraineeEntity> traineeStorage;
    private Map<String, TrainerEntity> trainerStorage;

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setUserStorage(Map<String, UserDao> userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    public void setTraineeStorage(Map<String, TraineeEntity> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(Map<String, TrainerEntity> trainerStorage) {
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
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("123 Main St");

        TraineeModel createdTraineeModel = gymFacade.createTrainee(traineeRequest);

        assertNotNull(createdTraineeModel);
        assertEquals("123 Main St", createdTraineeModel.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), createdTraineeModel.getDateOfBirth());
        assertNotNull(createdTraineeModel.getUsername());
    }

    @Test
    void testGetTrainee() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Jane");
        traineeRequest.setLastName("Smith");
        traineeRequest.setDateOfBirth(LocalDate.of(1995, 5, 15));
        traineeRequest.setAddress("456 Oak Ave");

        TraineeModel created = gymFacade.createTrainee(traineeRequest);
        String userId = created.getUsername();

        TraineeModel retrieved = gymFacade.getTrainee(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUsername());
        assertEquals("456 Oak Ave", retrieved.getAddress());
    }

    @Test
    void testUpdateTrainee() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Bob");
        traineeRequest.setLastName("Johnson");
        traineeRequest.setDateOfBirth(LocalDate.of(1985, 3, 10));
        traineeRequest.setAddress("789 Pine Rd");

        TraineeModel created = gymFacade.createTrainee(traineeRequest);
        String userId = created.getUsername();

        TraineeRequest updateDto = new TraineeRequest();
        updateDto.setFirstName("Bob");
        updateDto.setLastName("Johnson");
        updateDto.setDateOfBirth(LocalDate.of(1985, 3, 10));
        updateDto.setAddress("999 New Address");

        TraineeModel updated = gymFacade.updateTrainee(updateDto, userId);

        assertNotNull(updated);
        assertEquals("999 New Address", updated.getAddress());
    }

    @Test
    void testDeleteTrainee() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Alice");
        traineeRequest.setLastName("Williams");
        traineeRequest.setDateOfBirth(LocalDate.of(1992, 7, 20));
        traineeRequest.setAddress("321 Elm St");

        TraineeModel created = gymFacade.createTrainee(traineeRequest);
        String userId = created.getUsername();

        gymFacade.deleteTrainee(userId);
        assertThrows(NoSuchElementException.class, () -> gymFacade.getTrainee(userId));
    }

    // ============= Trainer =============

    @Test
    void testCreateTrainer() {
        TrainerRequest trainerDto = new TrainerRequest();
        trainerDto.setFirstName("Mike");
        trainerDto.setLastName("Tyson");
        trainerDto.setSpecialization("Boxing");
        trainerDto.setIsActive(true);

        TrainerModel createdTrainerModel = gymFacade.createTrainer(trainerDto);

        assertNotNull(createdTrainerModel);
        assertEquals("Boxing", createdTrainerModel.getSpecialization());
        assertNotNull(createdTrainerModel.getUsername());
    }

    @Test
    void testGetTrainer() {
        TrainerRequest trainerDto = new TrainerRequest();
        trainerDto.setFirstName("Sarah");
        trainerDto.setLastName("Connor");
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(true);

        TrainerModel created = gymFacade.createTrainer(trainerDto);
        String userId = created.getUsername();

        TrainerModel retrieved = gymFacade.getTrainer(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUsername());
        assertEquals("Fitness", retrieved.getSpecialization());
    }

    @Test
    void testUpdateTrainer() {
        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Tom");
        trainerRequest.setLastName("Hardy");
        trainerRequest.setSpecialization("Yoga");
        trainerRequest.setIsActive(true);

        TrainerModel created = gymFacade.createTrainer(trainerRequest);
        String userId = created.getUsername();

        TrainerRequest updateRequest = new TrainerRequest();
        updateRequest.setFirstName("Tom");
        updateRequest.setLastName("Hardy");
        updateRequest.setSpecialization("Pilates");
        updateRequest.setIsActive(true);

        TrainerModel updated = gymFacade.updateTrainer(updateRequest, userId);

        assertNotNull(updated);
        assertEquals("Pilates", updated.getSpecialization());
    }

    // ============= Training =============

    @Test
    void testCreateTraining() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Student");
        traineeRequest.setLastName("One");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("Address");
        TraineeModel traineeModel = gymFacade.createTrainee(traineeRequest);

        TrainerRequest trainerDto = new TrainerRequest();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("One");
        trainerDto.setSpecialization("Cardio");
        trainerDto.setIsActive(true);
        TrainerModel trainerModel = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainerModel.getUsername());
        trainingId.setTraineeId(traineeModel.getUsername());
        trainingId.setTrainingName("Morning Session");

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTrainingId(trainingId);
        trainingRequest.setTrainingType("Cardio");
        trainingRequest.setTrainingDate(LocalDate.now());
        trainingRequest.setTrainingDuration("60");

        TrainingModel created = gymFacade.createTraining(trainingRequest);

        assertNotNull(created);
        assertEquals("Cardio", created.getTrainingType());
        assertEquals("60", created.getTrainingDuration());
    }

    @Test
    void testGetTraining() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("Student");
        traineeRequest.setLastName("Two");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("Address");
        TraineeModel traineeModel = gymFacade.createTrainee(traineeRequest);

        TrainerRequest trainerDto = new TrainerRequest();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("Two");
        trainerDto.setSpecialization("Strength");
        trainerDto.setIsActive(true);
        TrainerModel trainerModel = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainerModel.getUsername());
        trainingId.setTraineeId(traineeModel.getUsername());
        trainingId.setTrainingName("Evening Session");

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTrainingId(trainingId);
        trainingRequest.setTrainingType("Strength");
        trainingRequest.setTrainingDate(LocalDate.now());
        trainingRequest.setTrainingDuration("90");

        gymFacade.createTraining(trainingRequest);

        TrainingModel retrieved = gymFacade.getTraining(trainingId);

        assertNotNull(retrieved);
        assertEquals("Strength", retrieved.getTrainingType());
        assertEquals("90", retrieved.getTrainingDuration());
    }

    @Test
    void testFacadeIsNotNull() {
        assertNotNull(gymFacade);
    }
}