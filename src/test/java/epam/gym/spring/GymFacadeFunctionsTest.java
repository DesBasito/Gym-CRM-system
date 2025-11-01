package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.config.StorageConfig;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.dto.TrainerDto;
import epam.gym.domain.dto.TrainingDto;
import epam.gym.domain.entities.*;
import epam.gym.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class, StorageConfig.class})
class GymFacadeFunctionsTest {
    private GymFacade gymFacade;
    private Map<String, User> userStorage;
    private Map<String, Trainee> traineeStorage;
    private Map<String, Trainer> trainerStorage;

    @Autowired
    public void setGymFacade(GymFacade gymFacade) {
        this.gymFacade = gymFacade;
    }

    @Autowired
    public void setUserStorage(Map<String, User> userStorage) {
        this.userStorage = userStorage;
    }

    @Autowired
    public void setTraineeStorage(Map<String, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Autowired
    public void setTrainerStorage(Map<String, Trainer> trainerStorage) {
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
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("John");
        traineeDto.setLastName("Doe");
        traineeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeDto.setAddress("123 Main St");

        Trainee createdTrainee = gymFacade.createTrainee(traineeDto);

        assertNotNull(createdTrainee);
        assertEquals("123 Main St", createdTrainee.getAddress());
        assertEquals(LocalDate.of(1990, 1, 1), createdTrainee.getDateOfBirth());
        assertNotNull(createdTrainee.getUserId());
    }

    @Test
    void testGetTrainee() {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("Jane");
        traineeDto.setLastName("Smith");
        traineeDto.setDateOfBirth(LocalDate.of(1995, 5, 15));
        traineeDto.setAddress("456 Oak Ave");

        Trainee created = gymFacade.createTrainee(traineeDto);
        String userId = created.getUserId();

        Trainee retrieved = gymFacade.getTrainee(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUserId());
        assertEquals("456 Oak Ave", retrieved.getAddress());
    }

    @Test
    void testUpdateTrainee() {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("Bob");
        traineeDto.setLastName("Johnson");
        traineeDto.setDateOfBirth(LocalDate.of(1985, 3, 10));
        traineeDto.setAddress("789 Pine Rd");

        Trainee created = gymFacade.createTrainee(traineeDto);
        String userId = created.getUserId();

        TraineeDto updateDto = new TraineeDto();
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
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("Alice");
        traineeDto.setLastName("Williams");
        traineeDto.setDateOfBirth(LocalDate.of(1992, 7, 20));
        traineeDto.setAddress("321 Elm St");

        Trainee created = gymFacade.createTrainee(traineeDto);
        String userId = created.getUserId();

        gymFacade.deleteTrainee(userId);

        Trainee deleted = gymFacade.getTrainee(userId);
        assertNull(deleted);
    }

    // ============= Trainer =============

    @Test
    void testCreateTrainer() {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("Mike");
        trainerDto.setLastName("Tyson");
        trainerDto.setSpecialization("Boxing");
        trainerDto.setIsActive(true);

        Trainer createdTrainer = gymFacade.createTrainer(trainerDto);

        assertNotNull(createdTrainer);
        assertEquals("Boxing", createdTrainer.getSpecialization());
        assertNotNull(createdTrainer.getUserId());
    }

    @Test
    void testGetTrainer() {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("Sarah");
        trainerDto.setLastName("Connor");
        trainerDto.setSpecialization("Fitness");
        trainerDto.setIsActive(true);

        Trainer created = gymFacade.createTrainer(trainerDto);
        String userId = created.getUserId();

        Trainer retrieved = gymFacade.getTrainer(userId);

        assertNotNull(retrieved);
        assertEquals(userId, retrieved.getUserId());
        assertEquals("Fitness", retrieved.getSpecialization());
    }

    @Test
    void testUpdateTrainer() {
        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("Tom");
        trainerDto.setLastName("Hardy");
        trainerDto.setSpecialization("Yoga");
        trainerDto.setIsActive(true);

        Trainer created = gymFacade.createTrainer(trainerDto);
        String userId = created.getUserId();

        TrainerDto updateDto = new TrainerDto();
        updateDto.setFirstName("Tom");
        updateDto.setLastName("Hardy");
        updateDto.setSpecialization("Pilates");
        updateDto.setIsActive(true);

        Trainer updated = gymFacade.updateTrainer(updateDto, userId);

        assertNotNull(updated);
        assertEquals("Pilates", updated.getSpecialization());
    }

    // ============= Training =============

    @Test
    void testCreateTraining() {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("Student");
        traineeDto.setLastName("One");
        traineeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeDto.setAddress("Address");
        Trainee trainee = gymFacade.createTrainee(traineeDto);

        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("One");
        trainerDto.setSpecialization("Cardio");
        trainerDto.setIsActive(true);
        Trainer trainer = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainer.getUserId());
        trainingId.setTraineeId(trainee.getUserId());
        trainingId.setTrainingName("Morning Session");

        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainingId(trainingId);
        trainingDto.setTrainingType("Cardio");
        trainingDto.setTrainingDate(LocalDate.now());
        trainingDto.setTrainingDuration("60");

        Training created = gymFacade.createTraining(trainingDto);

        assertNotNull(created);
        assertEquals("Cardio", created.getTrainingType());
        assertEquals("60", created.getTrainingDuration());
    }

    @Test
    void testGetTraining() {
        TraineeDto traineeDto = new TraineeDto();
        traineeDto.setFirstName("Student");
        traineeDto.setLastName("Two");
        traineeDto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeDto.setAddress("Address");
        Trainee trainee = gymFacade.createTrainee(traineeDto);

        TrainerDto trainerDto = new TrainerDto();
        trainerDto.setFirstName("Coach");
        trainerDto.setLastName("Two");
        trainerDto.setSpecialization("Strength");
        trainerDto.setIsActive(true);
        Trainer trainer = gymFacade.createTrainer(trainerDto);

        EmbeddedTrainingId trainingId = new EmbeddedTrainingId();
        trainingId.setTrainerId(trainer.getUserId());
        trainingId.setTraineeId(trainee.getUserId());
        trainingId.setTrainingName("Evening Session");

        TrainingDto trainingDto = new TrainingDto();
        trainingDto.setTrainingId(trainingId);
        trainingDto.setTrainingType("Strength");
        trainingDto.setTrainingDate(LocalDate.now());
        trainingDto.setTrainingDuration("90");

        gymFacade.createTraining(trainingDto);

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