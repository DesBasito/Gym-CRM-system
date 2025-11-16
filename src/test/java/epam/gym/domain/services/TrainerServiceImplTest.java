package epam.gym.domain.services;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.impl.TrainerServiceImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.mappers.TrainerMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private TrainerRequest trainerRequest;
    private TrainerModel trainerModel;
    private Trainer trainer;
    private User user;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("John");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("FITNESS");
        trainerRequest.setIsActive(true);

        trainerModel = new TrainerModel();
        trainerModel.setId(1L);
        trainerModel.setFirstName("John");
        trainerModel.setLastName("Smith");
        trainerModel.setUsername("John.Smith");
        trainerModel.setPassword("password123");
        trainerModel.setIsActive(true);
        trainerModel.setSpecialization("FITNESS");

        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setUsername("John.Smith");
        user.setPassword("password123");
        user.setIsActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("FITNESS");

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
    }

    @Test
    void testCreate_withValidSpecialization_shouldCreateTrainer() {
        when(trainerMapper.requestToModel(trainerRequest)).thenReturn(trainerModel);
        when(trainerMapper.toEntity(trainerModel)).thenReturn(trainer);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toModel(trainer)).thenReturn(trainerModel);

        TrainerModel result = trainerService.create(trainerRequest);

        assertNotNull(result);
        assertEquals("John.Smith", result.getUsername());
        assertEquals("FITNESS", result.getSpecialization());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testCreate_withInvalidSpecialization_shouldThrowException() {
        trainerRequest.setSpecialization("INVALID_TYPE");

        assertThrows(IllegalArgumentException.class, () ->
            trainerService.create(trainerRequest)
        );
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testUpdate_withValidSpecialization_shouldUpdateTrainer() {
        Long trainerId = 1L;
        trainerRequest.setFirstName("Jane");
        trainerRequest.setSpecialization("YOGA");

        TrainingType yogaType = new TrainingType();
        yogaType.setId(2L);
        yogaType.setTrainingTypeName("YOGA");

        when(trainerRepository.findById(trainerId)).thenReturn(trainer);
        when(trainingTypeRepository.findByName("YOGA")).thenReturn(yogaType);
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toModel(trainer)).thenReturn(trainerModel);

        TrainerModel result = trainerService.update(trainerRequest, trainerId);

        assertNotNull(result);
        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals(yogaType, trainer.getSpecialization());
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).save(trainer);
        verify(trainingTypeRepository).findByName("YOGA");
    }

    @Test
    void testUpdate_withInvalidSpecialization_shouldThrowException() {
        Long trainerId = 1L;
        trainerRequest.setSpecialization("INVALID_TYPE");

        assertThrows(IllegalArgumentException.class, () ->
            trainerService.update(trainerRequest, trainerId)
        );
        verify(trainerRepository, never()).findById(any());
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testUpdate_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        when(trainerRepository.findById(trainerId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.update(trainerRequest, trainerId)
        );
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testSelect_whenTrainerExists_shouldReturnTrainer() {
        Long trainerId = 1L;
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);
        when(trainerMapper.toModel(trainer)).thenReturn(trainerModel);

        TrainerModel result = trainerService.select(trainerId);

        assertNotNull(result);
        assertEquals(trainerId, result.getId());
        verify(trainerRepository).findById(trainerId);
        verify(trainerMapper).toModel(trainer);
    }

    @Test
    void testSelect_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        when(trainerRepository.findById(trainerId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.select(trainerId)
        );
        verify(trainerRepository).findById(trainerId);
    }

    @Test
    void testActivate_whenTrainerIsInactive_shouldActivate() {
        Long trainerId = 1L;
        user.setIsActive(false);
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);
        doNothing().when(trainerRepository).activate(trainerId);

        trainerService.activate(trainerId);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).activate(trainerId);
    }

    @Test
    void testActivate_whenTrainerIsActive_shouldSkipActivation() {
        Long trainerId = 1L;
        user.setIsActive(true);
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);

        trainerService.activate(trainerId);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).activate(trainerId);
    }

    @Test
    void testActivate_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        when(trainerRepository.findById(trainerId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.activate(trainerId)
        );
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).activate(any(Long.class));
    }

    @Test
    void testDeactivate_whenTrainerIsActive_shouldDeactivate() {
        Long trainerId = 1L;
        user.setIsActive(true);
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);
        doNothing().when(trainerRepository).deactivate(trainerId);

        trainerService.deactivate(trainerId);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).deactivate(trainerId);
    }

    @Test
    void testDeactivate_whenTrainerIsInactive_shouldSkipDeactivation() {
        Long trainerId = 1L;
        user.setIsActive(false);
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);

        trainerService.deactivate(trainerId);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).deactivate(trainerId);
    }

    @Test
    void testDeactivate_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        when(trainerRepository.findById(trainerId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.deactivate(trainerId)
        );
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).deactivate(any(Long.class));
    }

    @Test
    void testChangePassword_whenTrainerExists_shouldChangePassword() {
        Long trainerId = 1L;
        String newPassword = "newPassword123";
        when(trainerRepository.findById(trainerId)).thenReturn(trainer);
        doNothing().when(trainerRepository).changePassword(trainerId, newPassword);

        trainerService.changePassword(trainerId, newPassword);

        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).changePassword(trainerId, newPassword);
    }

    @Test
    void testChangePassword_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        String newPassword = "newPassword123";
        when(trainerRepository.findById(trainerId)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.changePassword(trainerId, newPassword)
        );
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).changePassword(any(Long.class), any(String.class));
    }

    @Test
    void testFindAllNotAssignedToTrainee_whenTraineeExists_shouldReturnTrainers() {
        String traineeUsername = "John.Doe";

        User traineeUser = new User();
        traineeUser.setUsername(traineeUsername);
        Trainee trainee = new Trainee();
        trainee.setUser(traineeUser);

        Trainer trainer1 = new Trainer();
        trainer1.setId(1L);
        Trainer trainer2 = new Trainer();
        trainer2.setId(2L);
        List<Trainer> trainers = Arrays.asList(trainer1, trainer2);

        TrainerModel model1 = new TrainerModel();
        model1.setId(1L);
        TrainerModel model2 = new TrainerModel();
        model2.setId(2L);

        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(trainee);
        when(trainerRepository.findAllNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);
        when(trainerMapper.toModel(trainer1)).thenReturn(model1);
        when(trainerMapper.toModel(trainer2)).thenReturn(model2);

        List<TrainerModel> result = trainerService.findAllNotAssignedToTrainee(traineeUsername);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(traineeRepository).findByUsername(traineeUsername);
        verify(trainerRepository).findAllNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testFindAllNotAssignedToTrainee_whenTraineeNotExists_shouldThrowException() {
        String traineeUsername = "NonExistent.User";
        when(traineeRepository.findByUsername(traineeUsername)).thenReturn(null);

        assertThrows(NoSuchElementException.class, () ->
            trainerService.findAllNotAssignedToTrainee(traineeUsername)
        );
        verify(traineeRepository).findByUsername(traineeUsername);
        verify(trainerRepository, never()).findAllNotAssignedToTrainee(any());
    }
}
