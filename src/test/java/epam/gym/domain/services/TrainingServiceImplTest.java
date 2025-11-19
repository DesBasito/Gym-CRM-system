package epam.gym.domain.services;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.impl.TrainingServiceImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.mappers.TrainingMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private TrainingRequest trainingRequest;
    private Training training;
    private TrainingModel trainingModel;
    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingRequest = new TrainingRequest();
        trainingRequest.setTraineeUsername("John.Doe");
        trainingRequest.setTrainerUsername("Jane.Smith");
        trainingRequest.setTrainingName("Morning Workout");
        trainingRequest.setTrainingType("FITNESS");
        trainingRequest.setTrainingDate(LocalDate.of(2024, 1, 15));
        trainingRequest.setTrainingDuration(60);

        trainee = new Trainee();
        trainee.setId(1L);

        trainer = new Trainer();
        trainer.setId(1L);

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
        when(trainingTypeRepository.findByName("FITNESS")).thenReturn(trainingType);
        when(traineeRepository.findByUsername("John.Doe")).thenReturn(trainee);
        when(trainerRepository.findByUsername("Jane.Smith")).thenReturn(trainer);
        when(trainingRepository.save(training)).thenReturn(training);
        when(trainingMapper.entityToModel(training)).thenReturn(trainingModel);

        TrainingModel result = trainingService.create(trainingRequest);

        assertNotNull(result);
        assertEquals("Morning Workout", result.getTrainingName());
        assertEquals(1L, result.getTrainingTypeId());
        assertEquals(1L, result.getTraineeId());
        assertEquals(1L, result.getTrainerId());
        verify(trainingRepository).save(any(Training.class));
        verify(trainingTypeRepository).findByName("FITNESS");
        verify(traineeRepository).findByUsername("John.Doe");
        verify(trainerRepository).findByUsername("Jane.Smith");
    }

    @Test
    void testCreate_withInvalidTrainingType_shouldThrowException() {
        trainingRequest.setTrainingType("INVALID_TYPE");

        assertThrows(IllegalArgumentException.class, () ->
            trainingService.create(trainingRequest)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testSelectTraineeTrainings_withAllParameters_shouldReturnTrainings() {
        String traineeUsername = "John.Doe";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);
        String trainingTypeName = "FITNESS";

        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> trainings = Arrays.asList(training1, training2);

        TrainingModel model1 = new TrainingModel();
        model1.setId(1L);
        TrainingModel model2 = new TrainingModel();
        model2.setId(2L);

        when(trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainingTypeName))
                .thenReturn(trainings);
        when(trainingMapper.entityToModel(training1)).thenReturn(model1);
        when(trainingMapper.entityToModel(training2)).thenReturn(model2);

        List<TrainingModel> result = trainingService.selectTraineeTrainings(
                traineeUsername, fromDate, toDate, trainingTypeName);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingRepository).findTraineeTrainings(traineeUsername, fromDate, toDate, trainingTypeName);
        verify(trainingMapper, times(2)).entityToModel(any(Training.class));
    }

    @Test
    void testSelectTraineeTrainings_withNullDates_shouldReturnTrainings() {
        String traineeUsername = "John.Doe";

        Training training1 = new Training();
        training1.setId(1L);
        List<Training> trainings = List.of(training1);

        TrainingModel model1 = new TrainingModel();
        model1.setId(1L);

        when(trainingRepository.findTraineeTrainings(traineeUsername, null, null, null))
                .thenReturn(trainings);
        when(trainingMapper.entityToModel(training1)).thenReturn(model1);

        List<TrainingModel> result = trainingService.selectTraineeTrainings(
                traineeUsername, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findTraineeTrainings(traineeUsername, null, null, null);
    }

    @Test
    void testSelectTraineeTrainings_whenNoTrainingsFound_shouldReturnEmptyList() {
        String traineeUsername = "John.Doe";
        when(trainingRepository.findTraineeTrainings(traineeUsername, null, null, null))
                .thenReturn(List.of());

        List<TrainingModel> result = trainingService.selectTraineeTrainings(
                traineeUsername, null, null, null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingRepository).findTraineeTrainings(traineeUsername, null, null, null);
    }

    @Test
    void testSelectTrainerTrainings_withAllParameters_shouldReturnTrainings() {
        String trainerUsername = "Jane.Smith";
        LocalDate fromDate = LocalDate.of(2024, 1, 1);
        LocalDate toDate = LocalDate.of(2024, 12, 31);

        Training training1 = new Training();
        training1.setId(1L);
        Training training2 = new Training();
        training2.setId(2L);
        List<Training> trainings = Arrays.asList(training1, training2);

        TrainingModel model1 = new TrainingModel();
        model1.setId(1L);
        TrainingModel model2 = new TrainingModel();
        model2.setId(2L);

        when(trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate))
                .thenReturn(trainings);
        when(trainingMapper.entityToModel(training1)).thenReturn(model1);
        when(trainingMapper.entityToModel(training2)).thenReturn(model2);

        List<TrainingModel> result = trainingService.selectTrainerTrainings(
                trainerUsername, fromDate, toDate);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(trainingRepository).findTrainerTrainings(trainerUsername, fromDate, toDate);
        verify(trainingMapper, times(2)).entityToModel(any(Training.class));
    }

    @Test
    void testSelectTrainerTrainings_withNullDates_shouldReturnTrainings() {
        String trainerUsername = "Jane.Smith";

        Training training1 = new Training();
        training1.setId(1L);
        List<Training> trainings = List.of(training1);

        TrainingModel model1 = new TrainingModel();
        model1.setId(1L);

        when(trainingRepository.findTrainerTrainings(trainerUsername, null, null))
                .thenReturn(trainings);
        when(trainingMapper.entityToModel(training1)).thenReturn(model1);

        List<TrainingModel> result = trainingService.selectTrainerTrainings(
                trainerUsername, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(trainingRepository).findTrainerTrainings(trainerUsername, null, null);
    }

    @Test
    void testSelectTrainerTrainings_whenNoTrainingsFound_shouldReturnEmptyList() {
        String trainerUsername = "Jane.Smith";
        when(trainingRepository.findTrainerTrainings(trainerUsername, null, null))
                .thenReturn(List.of());

        List<TrainingModel> result = trainingService.selectTrainerTrainings(
                trainerUsername, null, null);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(trainingRepository).findTrainerTrainings(trainerUsername, null, null);
    }
}
