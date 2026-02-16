package epam.gym.domain.services;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.impl.TrainingServiceImpl;
import epam.gym.domain.services.interfaces.WorkloadService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.mappers.TrainingMapper;
import epam.gym.infrastructure.monitoring.metrics.TrainingMetrics;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private TrainingMetrics trainingMetrics;

    @Mock
    private WorkloadService workloadService;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingRequest trainingRequest;
    private Training training;
    private TrainingModel trainingModel;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private User traineeUser;
    private User trainerUser;

    @BeforeEach
    void setUp() {
        trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername("John.Doe");
        trainingRequest.setTrainerUsername("Jane.Smith");
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        traineeUser = new User();
        traineeUser.setId(1L);
        traineeUser.setUsername("John.Doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setIsActive(true);

        trainee = new Trainee();
        trainee.setId(1L);
        trainee.setUser(traineeUser);

        trainerUser = new User();
        trainerUser.setId(2L);
        trainerUser.setUsername("Jane.Smith");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainerUser.setIsActive(true);

        trainer = new Trainer();
        trainer.setId(1L);
        trainer.setUser(trainerUser);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);

        training = new Training();
        training.setId(1L);
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingName("Morning Workout");
        training.setTrainingType(trainingType);
        training.setTrainingDate(LocalDate.of(2024, 1, 15));
        training.setTrainingDuration(60);

        trainingModel = new TrainingModel();
        trainingModel.setId(1L);
        trainingModel.setTraineeId(1L);
        trainingModel.setTrainerId(1L);
        trainingModel.setTrainingName("Morning Workout");
        trainingModel.setTrainingTypeId(1L);
        trainingModel.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingModel.setTrainingDuration("60");
    }

    @Test
    void testCreate_withValidData_shouldCreateTraining() {
        when(trainingMapper.requestToEntity(trainingRequest)).thenReturn(training);
        when(trainingTypeRepository
                .findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.valueOf("FITNESS")))
                .thenReturn(Optional.of(trainingType));
        when(traineeRepository.findByUser_Username("John.Doe")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUser_Username("Jane.Smith")).thenReturn(Optional.of(trainer));
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingMapper.entityToModel(training)).thenReturn(trainingModel);

        TrainingModel result = trainingService.create(trainingRequest);

        assertNotNull(result);
        assertEquals("Morning Workout", result.getTrainingName());
        assertEquals(1L, result.getTrainingTypeId());
        assertEquals(1L, result.getTraineeId());
        assertEquals(1L, result.getTrainerId());
        verify(trainingRepository).save(any(Training.class));
        verify(trainingTypeRepository).findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);
        verify(traineeRepository).findByUser_Username("John.Doe");
        verify(trainerRepository).findByUser_Username("Jane.Smith");
        verify(trainingMetrics).incrementTrainingCreated();
        verify(trainingMetrics).incrementActiveTrainings();
    }

    @Test
    void testSelectTraineeTrainings_withAllParameters_shouldReturnTrainings() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "John.Doe",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "Jane.Smith",
                "FITNESS"
        );

        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> trainings = Arrays.asList(training1, training2);

        TrainingDto dto1 = new TrainingDto();
        TrainingDto dto2 = new TrainingDto();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(trainings);
        when(trainingMapper.entityToDto(training1)).thenReturn(dto1);
        when(trainingMapper.entityToDto(training2)).thenReturn(dto2);

        List<TrainingDto> result = trainingService.selectTraineeTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
        verify(trainingMapper, times(2)).entityToDto(any(Training.class));
    }

    @Test
    void testSelectTraineeTrainings_withNullDates_shouldReturnTrainings() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "John.Doe", null, null, null, null
        );

        Training training1 = new Training();
        training1.setId(1L);
        List<Training> trainings = List.of(training1);

        TrainingDto dto1 = new TrainingDto();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(trainings);
        when(trainingMapper.entityToDto(training1)).thenReturn(dto1);

        List<TrainingDto> result = trainingService.selectTraineeTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void testSelectTraineeTrainings_whenNoTrainingsFound_shouldReturnEmptyList() {
        TraineeTrainingsFilterRequest filterRequest = new TraineeTrainingsFilterRequest(
                "John.Doe", null, null, null, null
        );

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        List<TrainingDto> result = trainingService.selectTraineeTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void testSelectTrainerTrainings_withAllParameters_shouldReturnTrainings() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "Jane.Smith",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                "John.Doe"
        );

        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> trainings = Arrays.asList(training1, training2);

        TrainingDto dto1 = new TrainingDto();
        TrainingDto dto2 = new TrainingDto();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(trainings);
        when(trainingMapper.entityToDto(training1)).thenReturn(dto1);
        when(trainingMapper.entityToDto(training2)).thenReturn(dto2);

        List<TrainingDto> result = trainingService.selectTrainerTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
        verify(trainingMapper, times(2)).entityToDto(any(Training.class));
    }

    @Test
    void testSelectTrainerTrainings_withNullDates_shouldReturnTrainings() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "Jane.Smith", null, null, null
        );

        Training training1 = new Training();
        training1.setId(1L);
        List<Training> trainings = List.of(training1);

        TrainingDto dto1 = new TrainingDto();

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(trainings);
        when(trainingMapper.entityToDto(training1)).thenReturn(dto1);

        List<TrainingDto> result = trainingService.selectTrainerTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
    }

    @Test
    void testSelectTrainerTrainings_whenNoTrainingsFound_shouldReturnEmptyList() {
        TrainerTrainingsFilterRequest filterRequest = new TrainerTrainingsFilterRequest(
                "Jane.Smith", null, null, null
        );

        when(trainingRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        List<TrainingDto> result = trainingService.selectTrainerTrainings(filterRequest);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingRepository).findAll(any(Specification.class));
    }
}
