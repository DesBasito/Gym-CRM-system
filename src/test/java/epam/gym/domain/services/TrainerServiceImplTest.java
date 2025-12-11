package epam.gym.domain.services;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TrainerInfoDto;
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
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        trainerRequest = new TrainerRequest();
        trainerRequest.setFirstName("John");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecialization("FITNESS");

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

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(user);
        trainer.setSpecialization(trainingType);
    }

    @Test
    void testCreate_withValidSpecialization_shouldCreateTrainerAndReturnCredentials() {
        TrainingType fitnessType = new TrainingType();
        fitnessType.setId(1L);
        fitnessType.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);

        when(trainerMapper.requestToModel(trainerRequest)).thenReturn(trainerModel);
        when(trainerMapper.toEntity(trainerModel)).thenReturn(trainer);
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.FITNESS))
                .thenReturn(Optional.of(fitnessType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        RegistrationResponse result = trainerService.create(trainerRequest);

        assertNotNull(result);
        assertEquals("John.Smith", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testUpdate_withValidSpecialization_shouldUpdateTrainer() {
        Long trainerId = 1L;
        trainerRequest.setFirstName("Jane");
        trainerRequest.setSpecialization("YOGA");
        epam.gym.constants.TrainingType type = epam.gym.constants.TrainingType.valueOf(trainerRequest.getSpecialization().toUpperCase());


        TrainingType yogaType = new TrainingType();
        yogaType.setId(2L);
        yogaType.setTrainingTypeName(epam.gym.constants.TrainingType.YOGA);

        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findTrainingTypeByTrainingTypeName(type)).thenReturn(Optional.of(yogaType));
        when(trainerRepository.save(trainer)).thenReturn(trainer);
        when(trainerMapper.toModel(trainer)).thenReturn(trainerModel);

        TrainerModel result = trainerService.update(trainerRequest, trainerId);

        assertNotNull(result);
        assertEquals("Jane", trainer.getUser().getFirstName());
        assertEquals(yogaType, trainer.getSpecialization());
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository).save(trainer);
        verify(trainingTypeRepository).findTrainingTypeByTrainingTypeName(type);
    }

    @Test
    void testUpdate_whenTrainerNotExists_shouldThrowException() {
        Long trainerId = 999L;
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            trainerService.update(trainerRequest, trainerId)
        );
        verify(trainerRepository).findById(trainerId);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testSelect_whenTrainerExists_shouldReturnTrainer() {
        Long trainerId = 1L;
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.of(trainer));
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
        when(trainerRepository.findById(trainerId)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            trainerService.select(trainerId)
        );
        verify(trainerRepository).findById(trainerId);
    }

    @Test
    void testSetActiveStatus_shouldActivateTrainer() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.setActiveStatus(username, true);

        verify(trainerRepository).findByUser_Username(username);
        verify(trainerRepository).save(trainer);
        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_shouldDeactivateTrainer() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.setActiveStatus(username, false);

        verify(trainerRepository).findByUser_Username(username);
        verify(trainerRepository).save(trainer);
        assertFalse(trainer.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenAlreadyActive_shouldStillActivate() {
        String username = "John.Doe";
        user.setIsActive(true);
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.setActiveStatus(username, true);

        verify(trainerRepository).findByUser_Username(username);
        verify(trainerRepository).save(trainer);
        assertTrue(trainer.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenAlreadyInactive_shouldStillDeactivate() {
        String username = "John.Doe";
        user.setIsActive(false);
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.setActiveStatus(username, false);

        verify(trainerRepository).findByUser_Username(username);
        verify(trainerRepository).save(trainer);
        assertFalse(trainer.getUser().getIsActive());
    }

    @Test
    void testSetActiveStatus_whenTrainerNotExists_shouldThrowException() {
        String username = "NonExistent.User";
        when(trainerRepository.findByUser_Username(username)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            trainerService.setActiveStatus(username, true)
        );
        verify(trainerRepository).findByUser_Username(username);
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testChangePassword_whenTrainerExists_shouldChangePassword() {
        String trainerUsername = "John.Smith";
        String oldPassword = "password123";
        String newPassword = "newPassword123";
        when(trainerRepository.authenticate(trainerUsername, oldPassword)).thenReturn(true);
        when(trainerRepository.findByUser_Username(trainerUsername)).thenReturn(Optional.of(trainer));
        when(trainerRepository.save(trainer)).thenReturn(trainer);

        trainerService.changePassword(trainerUsername, oldPassword, newPassword);

        verify(trainerRepository).authenticate(trainerUsername, oldPassword);
        verify(trainerRepository).findByUser_Username(trainerUsername);
        verify(trainerRepository).save(trainer);
        assertEquals(newPassword, trainer.getUser().getPassword());
    }

    @Test
    void testChangePassword_withInvalidOldPassword_shouldThrowException() {
        String trainerUsername = "John.Smith";
        String oldPassword = "wrongPassword";
        String newPassword = "newPassword123";
        when(trainerRepository.authenticate(trainerUsername, oldPassword)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
            trainerService.changePassword(trainerUsername, oldPassword, newPassword)
        );
        verify(trainerRepository).authenticate(trainerUsername, oldPassword);
        verify(trainerRepository, never()).findByUser_Username(any());
        verify(trainerRepository, never()).save(any());
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

        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.of(trainee));
        when(trainerRepository.findAllNotAssignedToTrainee(traineeUsername)).thenReturn(trainers);
        when(trainerMapper.toModel(trainer1)).thenReturn(model1);
        when(trainerMapper.toModel(trainer2)).thenReturn(model2);

        List<TrainerInfoDto> result = trainerService.findAllNotAssignedToTrainee(traineeUsername);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(traineeRepository).findByUser_Username(traineeUsername);
        verify(trainerRepository).findAllNotAssignedToTrainee(traineeUsername);
    }

    @Test
    void testFindAllNotAssignedToTrainee_whenTraineeNotExists_shouldThrowException() {
        String traineeUsername = "NonExistent.User";
        when(traineeRepository.findByUser_Username(traineeUsername)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () ->
            trainerService.findAllNotAssignedToTrainee(traineeUsername)
        );
        verify(traineeRepository).findByUser_Username(traineeUsername);
        verify(trainerRepository, never()).findAllNotAssignedToTrainee(any());
    }
}
