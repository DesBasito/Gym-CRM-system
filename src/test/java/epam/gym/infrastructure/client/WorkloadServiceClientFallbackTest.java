package epam.gym.infrastructure.client;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.client.dto.WorkloadRequest;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("WorkloadServiceClient Fallback Tests")
class WorkloadServiceClientFallbackTest {

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private WorkloadServiceClient workloadServiceClient;

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
    @DisplayName("Should create training successfully when workload-service is unavailable")
    void testCreateTraining_workloadServiceDown_shouldSucceed() {
        doThrow(FeignException.ServiceUnavailable.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2026, 2, 15));
        trainingRequest.setTrainingDuration(60);

        TrainingModel result = trainingService.create(trainingRequest);

        assertNotNull(result);
        assertEquals("Morning Workout", result.getTrainingName());
        assertNotNull(result.getTraineeId());
        assertNotNull(result.getTrainerId());

        verify(workloadServiceClient, times(1)).updateWorkload(any(WorkloadRequest.class));
    }

    @Test
    @DisplayName("Should create training successfully when workload-service returns error")
    void testCreateTraining_workloadServiceError_shouldSucceed() {
        doThrow(FeignException.InternalServerError.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Evening Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2026, 2, 16));
        trainingRequest.setTrainingDuration(45);

        TrainingModel result = trainingService.create(trainingRequest);

        assertNotNull(result);
        assertEquals("Evening Workout", result.getTrainingName());
        assertEquals("45", result.getTrainingDuration());

        verify(workloadServiceClient, times(1)).updateWorkload(any(WorkloadRequest.class));
    }

    @Test
    @DisplayName("Should create training successfully when workload-service times out")
    void testCreateTraining_workloadServiceTimeout_shouldSucceed() {
        doThrow(FeignException.GatewayTimeout.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        TrainingRequest trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Afternoon Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2026, 2, 17));
        trainingRequest.setTrainingDuration(90);

        TrainingModel result = trainingService.create(trainingRequest);

        assertNotNull(result);
        assertEquals("Afternoon Workout", result.getTrainingName());
        assertEquals("90", result.getTrainingDuration());

        verify(workloadServiceClient, times(1)).updateWorkload(any(WorkloadRequest.class));
    }
}
