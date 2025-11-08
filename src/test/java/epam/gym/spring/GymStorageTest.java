package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.config.StorageConfig;
import epam.gym.infrastructure.dao.*;
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
class GymStorageTest {
    private Map<String, UserDao> userStorage;
    private Map<String, TraineeDao> traineeStorage;
    private Map<String, TrainerDao> trainerStorage;
    private Map<EmbeddedTrainingDaoId, TrainingDao> trainingStorage;
    private Map<String, TrainingTypeDao> trainingTypeStorage;

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

    @Autowired
    public void setTrainingStorage(Map<EmbeddedTrainingDaoId, TrainingDao> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Autowired
    public void setTrainingTypeStorage(Map<String, TrainingTypeDao> trainingTypeStorage) {
        this.trainingTypeStorage = trainingTypeStorage;
    }

    @Test
    void testAllStoragesNotNull() {
        assertNotNull(userStorage);
        assertNotNull(traineeStorage);
        assertNotNull(trainerStorage);
        assertNotNull(trainingStorage);
        assertNotNull(trainingTypeStorage);
    }

    @Test
    void testAllStoragesNotEmpty() {
        assertFalse(userStorage.isEmpty());
        assertFalse(traineeStorage.isEmpty());
        assertFalse(trainerStorage.isEmpty());
        assertFalse(trainingStorage.isEmpty());
        assertFalse(trainingTypeStorage.isEmpty());
    }


    @Test
    void testUsersLoaded() {
        assertEquals(7, userStorage.size());

        UserDao johnDoe = userStorage.get("John.Doe");
        assertNotNull(johnDoe);
        assertEquals("John", johnDoe.getFirstName());
        assertEquals("Doe", johnDoe.getLastName());
        assertEquals("password123", johnDoe.getPassword());
        assertTrue(johnDoe.getIsActive());

        UserDao bobWilson = userStorage.get("Bob.Wilson");
        assertNotNull(bobWilson);
        assertFalse(bobWilson.getIsActive());
    }

    @Test
    void testTraineesLoaded() {
        assertEquals(3, traineeStorage.size());

        TraineeDao alice = traineeStorage.get("Alice.Brown");
        assertNotNull(alice);
        assertEquals(LocalDate.of(1990, 5, 15), alice.getDateOfBirth());
        assertEquals("123 Main St", alice.getAddress());
        assertEquals("Alice.Brown", alice.getUsername());

        TraineeDao bob = traineeStorage.get("Bob.Wilson");
        assertNotNull(bob);
        assertEquals(LocalDate.of(1995, 8, 20), bob.getDateOfBirth());
        assertEquals("456 Oak Ave", bob.getAddress());

        TraineeDao charlie = traineeStorage.get("Charlie.Davis");
        assertNotNull(charlie);
        assertEquals(LocalDate.of(1992, 3, 10), charlie.getDateOfBirth());
        assertEquals("789 Pine Rd", charlie.getAddress());
    }

    @Test
    void testTrainersLoaded() {
        assertEquals(4, trainerStorage.size());

        TrainerDao john = trainerStorage.get("John.Doe");
        assertNotNull(john);
        assertEquals("Fitness", john.getSpecialization());
        assertEquals("John.Doe", john.getUsername());

        TrainerDao jane = trainerStorage.get("Jane.Smith");
        assertNotNull(jane);
        assertEquals("Yoga", jane.getSpecialization());

        TrainerDao mike = trainerStorage.get("Mike.Johnson");
        assertNotNull(mike);
        assertEquals("Cardio", mike.getSpecialization());

        TrainerDao sarah = trainerStorage.get("Sarah.Connor");
        assertNotNull(sarah);
        assertEquals("Boxing", sarah.getSpecialization());
    }

    @Test
    void testTrainingsLoaded() {
        assertEquals(5, trainingStorage.size());

        EmbeddedTrainingDaoId id1 = new EmbeddedTrainingDaoId("John.Doe", "Alice.Brown", "Morning Workout");
        TrainingDao training1 = trainingStorage.get(id1);
        assertNotNull(training1);
        assertEquals("Fitness", training1.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 15), training1.getTrainingDate());
        assertEquals("60", training1.getTrainingDuration());

        EmbeddedTrainingDaoId id2 = new EmbeddedTrainingDaoId("Jane.Smith", "Bob.Wilson", "Yoga Session");
        TrainingDao training2 = trainingStorage.get(id2);
        assertNotNull(training2);
        assertEquals("Yoga", training2.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 16), training2.getTrainingDate());
        assertEquals("90", training2.getTrainingDuration());

        EmbeddedTrainingDaoId id3 = new EmbeddedTrainingDaoId("Mike.Johnson", "Alice.Brown", "Cardio Training");
        TrainingDao training3 = trainingStorage.get(id3);
        assertNotNull(training3);
        assertEquals("Cardio", training3.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 17), training3.getTrainingDate());
        assertEquals("45", training3.getTrainingDuration());

        EmbeddedTrainingDaoId id4 = new EmbeddedTrainingDaoId("Sarah.Connor", "Charlie.Davis", "Boxing Class");
        TrainingDao training4 = trainingStorage.get(id4);
        assertNotNull(training4);
        assertEquals("Boxing", training4.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 18), training4.getTrainingDate());
        assertEquals("75", training4.getTrainingDuration());

        EmbeddedTrainingDaoId id5 = new EmbeddedTrainingDaoId("John.Doe", "Charlie.Davis", "Evening Fitness");
        TrainingDao training5 = trainingStorage.get(id5);
        assertNotNull(training5);
        assertEquals("Fitness", training5.getTrainingType());
        assertEquals(LocalDate.of(2024, 1, 19), training5.getTrainingDate());
        assertEquals("50", training5.getTrainingDuration());
    }

    @Test
    void testTrainingTypesLoaded() {
        assertEquals(7, trainingTypeStorage.size());

        assertNotNull(trainingTypeStorage.get("Fitness"));
        assertNotNull(trainingTypeStorage.get("Yoga"));
        assertNotNull(trainingTypeStorage.get("Cardio"));
        assertNotNull(trainingTypeStorage.get("Boxing"));
        assertNotNull(trainingTypeStorage.get("Pilates"));
        assertNotNull(trainingTypeStorage.get("CrossFit"));
        assertNotNull(trainingTypeStorage.get("Swimming"));

        TrainingTypeDao fitness = trainingTypeStorage.get("Fitness");
        assertEquals("Fitness", fitness.getTrainingTypeName());
    }
    // ====================== Not Exists =====================

    @Test
    void testNonExistentUser() {
        assertNull(userStorage.get("NonExistent.User"));
    }

    @Test
    void testNonExistentTrainee() {
        assertNull(traineeStorage.get("NonExistent.TraineeDao"));
    }

    @Test
    void testNonExistentTrainer() {
        assertNull(trainerStorage.get("NonExistent.TrainerDao"));
    }

    @Test
    void testNonExistentTraining() {
        EmbeddedTrainingDaoId nonExistentId = new EmbeddedTrainingDaoId("Fake.TrainerDao", "Fake.TraineeDao", "Fake TrainingDao");
        assertNull(trainingStorage.get(nonExistentId));
    }

    @Test
    void testNonExistentTrainingType() {
        assertNull(trainingTypeStorage.get("NonExistentType"));
    }
}