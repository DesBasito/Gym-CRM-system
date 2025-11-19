package epam.gym.infrastructure.mappers;

import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TrainingModel;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.entities.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TrainingMapperTest {

    private TrainingMapper trainingMapper;

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Test
    void testRequestToEntity_shouldMapAllFields() {
        TrainingRequest request = new TrainingRequest();
        request.setTraineeUsername("John.Doe");
        request.setTrainerUsername("Jane.Smith");
        request.setTrainingName("Morning Workout");
        request.setTrainingType("FITNESS");
        request.setTrainingDate(LocalDate.of(2024, 1, 15));
        request.setTrainingDuration(60);

        Training entity = trainingMapper.requestToEntity(request);

        assertNotNull(entity);
        assertNull(entity.getId());
        assertEquals("Morning Workout", entity.getTrainingName());
        assertEquals(LocalDate.of(2024, 1, 15), entity.getTrainingDate());
        assertEquals(60, entity.getTrainingDuration());
        assertNull(entity.getTrainee());
        assertNull(entity.getTrainer());
        assertNull(entity.getTrainingType());
    }

    @Test
    void testEntityToModel_shouldMapAllFields() {
        Trainee trainee = new Trainee();
        trainee.setId(1L);

        Trainer trainer = new Trainer();
        trainer.setId(2L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(3L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);

        Training entity = new Training();
        entity.setId(10L);
        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setTrainingName("Morning Workout");
        entity.setTrainingType(trainingType);
        entity.setTrainingDate(LocalDate.of(2024, 1, 15));
        entity.setTrainingDuration(60);

        TrainingModel model = trainingMapper.entityToModel(entity);

        assertNotNull(model);
        assertEquals(10L, model.getId());
        assertEquals(1L, model.getTraineeId());
        assertEquals(2L, model.getTrainerId());
        assertEquals(3L, model.getTrainingTypeId());
        assertEquals("Morning Workout", model.getTrainingName());
        assertEquals(LocalDate.of(2024, 1, 15), model.getTrainingDate());
    }

    @Test
    void testEntityToModel_withDifferentData_shouldMapCorrectly() {
        Trainee trainee = new Trainee();
        trainee.setId(5L);

        Trainer trainer = new Trainer();
        trainer.setId(6L);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(7L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.YOGA);

        Training entity = new Training();
        entity.setId(20L);
        entity.setTrainee(trainee);
        entity.setTrainer(trainer);
        entity.setTrainingName("Evening Yoga");
        entity.setTrainingType(trainingType);
        entity.setTrainingDate(LocalDate.of(2024, 6, 20));
        entity.setTrainingDuration(90);

        TrainingModel model = trainingMapper.entityToModel(entity);

        assertNotNull(model);
        assertEquals(20L, model.getId());
        assertEquals(5L, model.getTraineeId());
        assertEquals(6L, model.getTrainerId());
        assertEquals(7L, model.getTrainingTypeId());
        assertEquals("Evening Yoga", model.getTrainingName());
        assertEquals(LocalDate.of(2024, 6, 20), model.getTrainingDate());
    }
}
