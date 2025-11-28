package epam.gym.infrastructure.mappers;

import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Test
    void testRequestToModel_shouldMapAllFields() {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        TraineeModel model = traineeMapper.requestToModel(request);

        assertNotNull(model);
        assertEquals("John", model.getFirstName());
        assertEquals("Doe", model.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), model.getDateOfBirth());
        assertEquals("123 Main St", model.getAddress());
        assertTrue(model.getIsActive());
    }

    @Test
    void testToEntity_shouldMapAllFields() {
        TraineeModel model = new TraineeModel();
        model.setId(1L);
        model.setFirstName("John");
        model.setLastName("Doe");
        model.setUsername("John.Doe");
        model.setPassword("password123");
        model.setIsActive(true);
        model.setDateOfBirth(LocalDate.of(1990, 1, 1));
        model.setAddress("123 Main St");

        Trainee entity = traineeMapper.toEntity(model);

        assertNotNull(entity);
        assertEquals(1L, entity.getId());
        assertNotNull(entity.getUser());
        assertEquals("John", entity.getUser().getFirstName());
        assertEquals("Doe", entity.getUser().getLastName());
        assertEquals("John.Doe", entity.getUser().getUsername());
        assertEquals("password123", entity.getUser().getPassword());
        assertTrue(entity.getUser().getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), entity.getDateOfBirth());
        assertEquals("123 Main St", entity.getAddress());
    }

    @Test
    void testToModel_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("password123");
        user.setIsActive(true);

        Trainee entity = new Trainee();
        entity.setId(2L);
        entity.setUser(user);
        entity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        entity.setAddress("123 Main St");

        TraineeModel model = traineeMapper.toModel(entity);

        assertNotNull(model);
        assertEquals(2L, model.getId());
        assertEquals(1L, model.getUserId());
        assertEquals("John", model.getFirstName());
        assertEquals("Doe", model.getLastName());
        assertEquals("John.Doe", model.getUsername());
        assertEquals("password123", model.getPassword());
        assertTrue(model.getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), model.getDateOfBirth());
        assertEquals("123 Main St", model.getAddress());
    }

    @Test
    void testToModel_withNullAddress_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("password123");
        user.setIsActive(true);

        Trainee entity = new Trainee();
        entity.setId(2L);
        entity.setUser(user);
        entity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        entity.setAddress(null);

        TraineeModel model = traineeMapper.toModel(entity);

        assertNotNull(model);
        assertNull(model.getAddress());
    }

    @Test
    void testToModel_withNullDateOfBirth_shouldMapCorrectly() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("John.Doe");
        user.setPassword("password123");
        user.setIsActive(true);

        Trainee entity = new Trainee();
        entity.setId(2L);
        entity.setUser(user);
        entity.setDateOfBirth(null);
        entity.setAddress("123 Main St");

        TraineeModel model = traineeMapper.toModel(entity);

        assertNotNull(model);
        assertNull(model.getDateOfBirth());
    }
}
