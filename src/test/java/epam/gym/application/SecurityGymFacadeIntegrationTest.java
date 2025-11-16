package epam.gym.application;

import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.exceptions.ForbiddenException;
import epam.gym.domain.exceptions.UnauthorizedException;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.models.TrainingModel;
import epam.gym.infrastructure.security.UserContext;
import epam.gym.infrastructure.security.UserRole;
import jakarta.persistence.EntityManager;
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
class SecurityGymFacadeIntegrationTest {
    private final GymFacade securityGymFacade;
    private final UserContext userContext;
    private final EntityManager entityManager;

    @Autowired
    public SecurityGymFacadeIntegrationTest(GymFacade securityGymFacade, UserContext userContext, EntityManager entityManager) {
        this.securityGymFacade = securityGymFacade;
        this.userContext = userContext;
        this.entityManager = entityManager;
    }

    @BeforeEach
    void setUp() {
        userContext.logout();
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
    void testCreateTrainee_withoutAuth_shouldWork() {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");
        request.setIsActive(true);

        TraineeModel result = securityGymFacade.createTrainee(request);

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
    }

    @Test
    void testUpdateTrainee_withoutAuth_shouldThrowException() {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        assertThrows(UnauthorizedException.class, () ->
                securityGymFacade.updateTrainee(request, 1L)
        );
    }

    @Test
    void testUpdateTrainee_withWrongRole_shouldThrowException() {
        userContext.login("Jane.Smith", UserRole.TRAINER);

        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        assertThrows(ForbiddenException.class, () ->
                securityGymFacade.updateTrainee(request, 1L)
        );
    }

    @Test
    void testUpdateTrainee_withCorrectAuthAndOwnership_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        TraineeRequest updateRequest = new TraineeRequest();
        updateRequest.setFirstName("Jane");
        updateRequest.setLastName("Doe");
        updateRequest.setAddress("456 Oak Ave");
        updateRequest.setIsActive(true);

        TraineeModel updated = securityGymFacade.updateTrainee(updateRequest, created.getId());

        assertNotNull(updated);
        assertEquals("Jane", updated.getFirstName());
        assertEquals("456 Oak Ave", updated.getAddress());
    }

    @Test
    void testGetTrainee_withoutAuth_shouldThrowException() {
        assertThrows(UnauthorizedException.class, () ->
                securityGymFacade.getTrainee(1L)
        );
    }

    @Test
    void testGetTrainee_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        TraineeModel result = securityGymFacade.getTrainee(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
    }

    @Test
    void testDeleteTrainee_withoutAuth_shouldThrowException() {
        assertThrows(UnauthorizedException.class, () ->
                securityGymFacade.deleteTrainee(1L)
        );
    }

    @Test
    void testDeleteTrainee_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        assertDoesNotThrow(() -> securityGymFacade.deleteTrainee(created.getId()));
    }

    @Test
    void testGetAllTrainees_withoutAuth_shouldThrowException() {
        assertThrows(UnauthorizedException.class, securityGymFacade::getAllTrainees
        );
    }

    @Test
    void testGetAllTrainees_withCorrectAuth_shouldWork() {
        userContext.login("John.Doe", UserRole.TRAINEE);

        List<TraineeModel> result = securityGymFacade.getAllTrainees();

        assertNotNull(result);
    }

    @Test
    void testAuthenticateTrainee_withoutAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);

        boolean result = securityGymFacade.authenticateTrainee(created.getUsername(), created.getPassword());

        assertTrue(result);
    }

    @Test
    void testChangeTraineePassword_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        assertDoesNotThrow(() ->
                securityGymFacade.changeTraineePassword(created.getId(), "newPassword123")
        );
    }

    @Test
    void testActivateTrainee_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(false);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        assertDoesNotThrow(() -> securityGymFacade.activateTrainee(created.getId()));
    }

    @Test
    void testDeactivateTrainee_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        assertDoesNotThrow(() -> securityGymFacade.deactivateTrainee(created.getId()));
    }

    @Test
    void testGetTraineeTrainings_withCorrectAuth_shouldWork() {
        TraineeRequest createRequest = new TraineeRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        createRequest.setAddress("123 Main St");
        createRequest.setIsActive(true);

        TraineeModel created = securityGymFacade.createTrainee(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINEE);

        List<TrainingModel> result = securityGymFacade.getTraineeTrainings(
                created.getUsername(), null, null, null);

        assertNotNull(result);
    }

    @Test
    void testCreateTrainer_withoutAuth_shouldWork() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("FITNESS");
        request.setIsActive(true);

        TrainerModel result = securityGymFacade.createTrainer(request);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
    }

    @Test
    void testUpdateTrainer_withCorrectAuth_shouldWork() {
        TrainerRequest createRequest = new TrainerRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setSpecialization("FITNESS");
        createRequest.setIsActive(true);

        TrainerModel created = securityGymFacade.createTrainer(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINER);

        TrainerRequest updateRequest = new TrainerRequest();
        updateRequest.setFirstName("Janet");
        updateRequest.setLastName("Smith");
        updateRequest.setSpecialization("YOGA");
        updateRequest.setIsActive(true);

        TrainerModel updated = securityGymFacade.updateTrainer(updateRequest, created.getId());

        assertNotNull(updated);
        assertEquals("Janet", updated.getFirstName());
    }

    @Test
    void testGetTrainer_withCorrectAuth_shouldWork() {
        TrainerRequest createRequest = new TrainerRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setSpecialization("FITNESS");
        createRequest.setIsActive(true);

        TrainerModel created = securityGymFacade.createTrainer(createRequest);
        userContext.login(created.getUsername(), UserRole.TRAINER);

        TrainerModel result = securityGymFacade.getTrainer(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
    }

    @Test
    void testGetAllTrainers_withCorrectAuth_shouldWork() {
        userContext.login("Jane.Smith", UserRole.TRAINER);

        List<TrainerModel> result = securityGymFacade.getAllTrainers();

        assertNotNull(result);
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_withCorrectAuth_shouldWork() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("123 Main St");
        traineeRequest.setIsActive(true);

        TraineeModel trainee = securityGymFacade.createTrainee(traineeRequest);
        userContext.login(trainee.getUsername(), UserRole.TRAINEE);

        List<TrainerModel> result = securityGymFacade.getTrainersNotAssignedToTrainee(trainee.getUsername());

        assertNotNull(result);
    }

    @Test
    void testAuthenticateTrainer_withoutAuth_shouldWork() {
        TrainerRequest createRequest = new TrainerRequest();
        createRequest.setFirstName("Jane");
        createRequest.setLastName("Smith");
        createRequest.setSpecialization("FITNESS");
        createRequest.setIsActive(true);

        TrainerModel created = securityGymFacade.createTrainer(createRequest);

        boolean result = securityGymFacade.authenticateTrainer(created.getUsername(), created.getPassword());

        assertTrue(result);
    }

    @Test
    void testCreateTraining_withAuth_shouldWork() {
        TraineeRequest traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        traineeRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeRequest.setAddress("123 Main St");
        traineeRequest.setIsActive(true);

        TrainerRequest trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("FITNESS");
        trainerRequest.setIsActive(true);

        TraineeModel trainee = securityGymFacade.createTrainee(traineeRequest);
        TrainerModel trainer = securityGymFacade.createTrainer(trainerRequest);

        userContext.login(trainee.getUsername(), UserRole.TRAINEE);

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(trainee.getUsername());
        trainingRequest.setTrainerUsername(trainer.getUsername());
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        TrainingModel result = securityGymFacade.createTraining(trainingRequest);

        assertNotNull(result);
        assertEquals("Morning Workout", result.getTrainingName());
    }
}
