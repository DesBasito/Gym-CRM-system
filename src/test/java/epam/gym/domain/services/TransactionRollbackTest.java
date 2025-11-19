package epam.gym.domain.services;

import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
class TransactionRollbackTest {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final EntityManager entityManager;

    @Autowired
    public TransactionRollbackTest(TraineeService traineeService, TrainerService trainerService, EntityManager entityManager) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();

        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainees ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainers ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 1").executeUpdate();
        entityManager.flush();
    }

    @Test
    void testTraineeUpdate_whenExceptionOccurs_shouldRollback() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = traineeService.create(createRequest);
        Long traineeId = created.getId();
        String originalFirstName = created.getFirstName();
        String originalAddress = created.getAddress();

        TraineeRequest updateRequest = new TraineeRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Doe");
        updateRequest.setAddress("456 Oak Ave");
        updateRequest.setIsActive(true);

        try {
            assertThrows(NoSuchElementException.class, () -> {
                traineeService.update(updateRequest, 999L);
            });
        } catch (Exception ignored) {
            System.out.println("Rollback transaction!");
        }

        TraineeModel afterFailedUpdate = traineeService.select(traineeId);
        assertEquals(originalFirstName, afterFailedUpdate.getFirstName());
        assertEquals(originalAddress, afterFailedUpdate.getAddress());
    }

    @Test
    void testTrainerUpdate_whenExceptionOccurs_shouldRollback() {
        TrainerRequest createRequest = new TrainerRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setSpecialization("FITNESS");
        createRequest.setIsActive(true);

        TrainerModel created = trainerService.create(createRequest);
        Long trainerId = created.getId();
        String originalFirstName = created.getFirstName();
        String originalSpecialization = created.getSpecialization();

        TrainerRequest updateRequest = new TrainerRequest();
        updateRequest.setFirstName("Janet");
        updateRequest.setLastName("Smith");
        updateRequest.setSpecialization("YOGA");
        updateRequest.setIsActive(true);

        try {
            assertThrows(NoSuchElementException.class, () -> {
                trainerService.update(updateRequest, 999L);
            });
        } catch (Exception ignored) {
            System.out.println("Rollback transaction!");
        }

        TrainerModel afterFailedUpdate = trainerService.select(trainerId);
        assertEquals(originalFirstName, afterFailedUpdate.getFirstName());
        assertEquals(originalSpecialization, afterFailedUpdate.getSpecialization());
    }
}
