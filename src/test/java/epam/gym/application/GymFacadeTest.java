package epam.gym.application;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.security.UserContext;
import epam.gym.infrastructure.security.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private GymFacade gymFacade;

    private TraineeRequest traineeRequest;
    private TraineeModel traineeModel;
    private TrainerRequest trainerRequest;
    private TrainerModel trainerModel;
    private TrainingRequest trainingRequest;
    private TrainingModel trainingModel;

    @BeforeEach
    void setUp() {
        traineeRequest = new TraineeRequest();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");

        traineeModel = new TraineeModel();
        traineeModel.setId(1L);
        traineeModel.setUsername("John.Doe");

        trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");

        trainerModel = new TrainerModel();
        trainerModel.setId(1L);
        trainerModel.setUsername("Jane.Smith");

        trainingRequest = new TrainingRequest();
        trainingRequest.setTrainingName("Morning Workout");

        trainingModel = new TrainingModel();
        trainingModel.setId(1L);
    }

    @Test
    void testCreateTrainee_shouldCallService() {
        when(traineeService.create(traineeRequest)).thenReturn(traineeModel);

        TraineeModel result = gymFacade.createTrainee(traineeRequest);

        assertNotNull(result);
        assertEquals("John.Doe", result.getUsername());
        verify(traineeService).create(traineeRequest);
    }

    @Test
    void testUpdateTrainee_shouldCallService() {
        Long traineeId = 1L;
        when(traineeService.update(traineeRequest, traineeId)).thenReturn(traineeModel);

        TraineeModel result = gymFacade.updateTrainee(traineeRequest, traineeId);

        assertNotNull(result);
        verify(traineeService).update(traineeRequest, traineeId);
    }

    @Test
    void testGetTrainee_shouldCallService() {
        Long traineeId = 1L;
        when(traineeService.select(traineeId)).thenReturn(traineeModel);

        TraineeModel result = gymFacade.getTrainee(traineeId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(traineeService).select(traineeId);
    }

    @Test
    void testDeleteTrainee_shouldCallService() {
        Long traineeId = 1L;
        doNothing().when(traineeService).delete(traineeId);

        gymFacade.deleteTrainee(traineeId);

        verify(traineeService).delete(traineeId);
    }

    @Test
    void testGetAllTrainees_shouldReturnAllTrainees() {
        Trainee trainee1 = new Trainee();
        trainee1.setId(1L);
        Trainee trainee2 = new Trainee();
        trainee2.setId(2L);
        List<Trainee> trainees = Arrays.asList(trainee1, trainee2);

        TraineeModel model1 = new TraineeModel();
        model1.setId(1L);
        TraineeModel model2 = new TraineeModel();
        model2.setId(2L);

        when(traineeRepository.findAll()).thenReturn(trainees);
        when(traineeService.select(1L)).thenReturn(model1);
        when(traineeService.select(2L)).thenReturn(model2);

        List<TraineeModel> result = gymFacade.getAllTrainees();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(traineeRepository).findAll();
        verify(traineeService, times(2)).select(any(Long.class));
    }

    @Test
    void testAuthenticateTrainee_whenSuccess_shouldLoginUser() {
        String username = "John.Doe";
        String password = "password123";
        when(traineeRepository.authenticate(username, password)).thenReturn(true);

        boolean result = gymFacade.authenticateTrainee(username, password);

        assertTrue(result);
        verify(traineeRepository).authenticate(username, password);
        verify(userContext).login(username, UserRole.TRAINEE);
    }

    @Test
    void testAuthenticateTrainee_whenFailed_shouldNotLoginUser() {
        String username = "John.Doe";
        String password = "wrongpassword";
        when(traineeRepository.authenticate(username, password)).thenReturn(false);

        boolean result = gymFacade.authenticateTrainee(username, password);

        assertFalse(result);
        verify(traineeRepository).authenticate(username, password);
        verify(userContext, never()).login(any(), any());
    }

    @Test
    void testChangeTraineePassword_shouldCallService() {
        Long traineeId = 1L;
        String newPassword = "newPassword123";
        doNothing().when(traineeService).changePassword(traineeId, newPassword);

        gymFacade.changeTraineePassword(traineeId, newPassword);

        verify(traineeService).changePassword(traineeId, newPassword);
    }

    @Test
    void testActivateTrainee_shouldCallService() {
        Long traineeId = 1L;
        doNothing().when(traineeService).activate(traineeId);

        gymFacade.activateTrainee(traineeId);

        verify(traineeService).activate(traineeId);
    }

    @Test
    void testDeactivateTrainee_shouldCallService() {
        Long traineeId = 1L;
        doNothing().when(traineeService).deactivate(traineeId);

        gymFacade.deactivateTrainee(traineeId);

        verify(traineeService).deactivate(traineeId);
    }

    @Test
    void testGetTraineeTrainings_shouldCallService() {
        String traineeUsername = "John.Doe";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        String trainingType = "FITNESS";

        List<TrainingModel> trainings = Collections.singletonList(trainingModel);
        when(trainingService.selectTraineeTrainings(traineeUsername, fromDate, toDate, trainingType))
                .thenReturn(trainings);

        List<TrainingModel> result = gymFacade.getTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).selectTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);
    }

    @Test
    void testCreateTrainer_shouldCallService() {
        when(trainerService.create(trainerRequest)).thenReturn(trainerModel);

        TrainerModel result = gymFacade.createTrainer(trainerRequest);

        assertNotNull(result);
        assertEquals("Jane.Smith", result.getUsername());
        verify(trainerService).create(trainerRequest);
    }

    @Test
    void testUpdateTrainer_shouldCallService() {
        Long trainerId = 1L;
        when(trainerService.update(trainerRequest, trainerId)).thenReturn(trainerModel);

        TrainerModel result = gymFacade.updateTrainer(trainerRequest, trainerId);

        assertNotNull(result);
        verify(trainerService).update(trainerRequest, trainerId);
    }

    @Test
    void testGetTrainer_shouldCallService() {
        Long trainerId = 1L;
        when(trainerService.select(trainerId)).thenReturn(trainerModel);

        TrainerModel result = gymFacade.getTrainer(trainerId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainerService).select(trainerId);
    }

    @Test
    void testGetAllTrainers_shouldReturnAllTrainers() {
        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        TrainerModel model1 = new TrainerModel();
        model1.setId(1L);
        TrainerModel model2 = new TrainerModel();
        model2.setId(2L);

        when(trainerRepository.findAll()).thenReturn(trainers);
        when(trainerService.select(1L)).thenReturn(model1);
        when(trainerService.select(2L)).thenReturn(model2);

        List<TrainerModel> result = gymFacade.getAllTrainers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainerRepository).findAll();
        verify(trainerService, times(2)).select(any(Long.class));
    }

    @Test
    void testGetTrainersNotAssignedToTrainee_shouldCallService() {
        String traineeUsername = "John.Doe";
        List<TrainerModel> trainers = Collections.singletonList(trainerModel);
        when(trainerService.findAllNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);

        List<TrainerModel> result = gymFacade.getTrainersNotAssignedToTrainee(traineeUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainerService).findAllNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testAuthenticateTrainer_whenSuccess_shouldLoginUser() {
        String username = "Jane.Smith";
        String password = "password123";
        when(trainerRepository.authenticate(username, password)).thenReturn(true);

        boolean result = gymFacade.authenticateTrainer(username, password);

        assertTrue(result);
        verify(trainerRepository).authenticate(username, password);
        verify(userContext).login(username, UserRole.TRAINER);
    }

    @Test
    void testAuthenticateTrainer_whenFailed_shouldNotLoginUser() {
        String username = "Jane.Smith";
        String password = "wrongpassword";
        when(trainerRepository.authenticate(username, password)).thenReturn(false);

        boolean result = gymFacade.authenticateTrainer(username, password);

        assertFalse(result);
        verify(trainerRepository).authenticate(username, password);
        verify(userContext, never()).login(any(), any());
    }

    @Test
    void testChangeTrainerPassword_shouldCallService() {
        Long trainerId = 1L;
        String newPassword = "newPassword123";
        doNothing().when(trainerService).changePassword(trainerId, newPassword);

        gymFacade.changeTrainerPassword(trainerId, newPassword);

        verify(trainerService).changePassword(trainerId, newPassword);
    }

    @Test
    void testActivateTrainer_shouldCallService() {
        Long trainerId = 1L;
        doNothing().when(trainerService).activate(trainerId);

        gymFacade.activateTrainer(trainerId);

        verify(trainerService).activate(trainerId);
    }

    @Test
    void testDeactivateTrainer_shouldCallService() {
        Long trainerId = 1L;
        doNothing().when(trainerService).deactivate(trainerId);

        gymFacade.deactivateTrainer(trainerId);

        verify(trainerService).deactivate(trainerId);
    }

    @Test
    void testGetTrainerTrainings_shouldCallService() {
        String trainerUsername = "Jane.Smith";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);

        List<TrainingModel> trainings = Collections.singletonList(trainingModel);
        when(trainingService.selectTrainerTrainings(trainerUsername, fromDate, toDate))
                .thenReturn(trainings);

        List<TrainingModel> result = gymFacade.getTrainerTrainings(trainerUsername, fromDate, toDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingService).selectTrainerTrainings(trainerUsername, fromDate, toDate);
    }

    @Test
    void testCreateTraining_shouldCallService() {
        when(trainingService.create(trainingRequest)).thenReturn(trainingModel);

        TrainingModel result = gymFacade.createTraining(trainingRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(trainingService).create(trainingRequest);
    }
}
