package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.TrainingType;
import epam.gym.infrastructure.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TrainerMapperTest {

    private TrainerMapper trainerMapper;

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Test
    void testRequestToModel_shouldMapAllFields() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setSpecialization("FITNESS");

        TrainerModel model = trainerMapper.requestToModel(request);

        assertNotNull(model);
        assertEquals("Jane", model.getFirstName());
        assertEquals("Smith", model.getLastName());
        assertEquals("FITNESS", model.getSpecialization());
        assertTrue(model.getIsActive());
    }

    @Test
    void testToEntity_shouldMapAllFields() {
        TrainerModel model = new TrainerModel();
        model.setId(1L);
        model.setFirstName("Jane");
        model.setLastName("Smith");
        model.setUsername("Jane.Smith");
        model.setPassword("password123");
        model.setIsActive(true);
        model.setSpecialization("FITNESS");

        Trainer entity = trainerMapper.toEntity(model);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertNotNull(entity.getUser());
        assertEquals("Jane", entity.getUser().getFirstName());
        assertEquals("Smith", entity.getUser().getLastName());
        assertEquals("Jane.Smith", entity.getUser().getUsername());
        assertEquals("password123", entity.getUser().getPassword());
        assertTrue(entity.getUser().getIsActive());
    }

    @Test
    void testToModel_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("Jane.Smith");
        user.setPassword("password123");
        user.setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.FITNESS);

        Trainer entity = new Trainer();
        entity.setId(2L);
        entity.setUser(user);
        entity.setSpecialization(trainingType);

        TrainerModel model = trainerMapper.toModel(entity);

        assertNotNull(model);
        assertEquals(2L, model.getId());
        assertEquals(1L, model.getUserId());
        assertEquals("Jane", model.getFirstName());
        assertEquals("Smith", model.getLastName());
        assertEquals("Jane.Smith", model.getUsername());
        assertEquals("password123", model.getPassword());
        assertTrue(model.getIsActive());
        assertEquals("FITNESS", model.getSpecialization());
    }

    @Test
    void testToModel_withDifferentSpecialization_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setUsername("Jane.Smith");
        user.setPassword("password123");
        user.setIsActive(false);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(2L);
        trainingType.setTrainingTypeName(epam.gym.constants.TrainingType.YOGA);

        Trainer entity = new Trainer();
        entity.setId(2L);
        entity.setUser(user);
        entity.setSpecialization(trainingType);

        TrainerModel model = trainerMapper.toModel(entity);

        assertNotNull(model);
        assertEquals("YOGA", model.getSpecialization());
        assertFalse(model.getIsActive());
    }
}
