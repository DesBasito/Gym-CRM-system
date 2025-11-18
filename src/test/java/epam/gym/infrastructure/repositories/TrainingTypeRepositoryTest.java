package epam.gym.infrastructure.repositories;

import epam.gym.config.TestConfig;
import epam.gym.infrastructure.entities.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@Transactional
class TrainingTypeRepositoryTest {
    private TrainingTypeRepository trainingTypeRepository;

    @Autowired
    public void setTrainingTypeRepository(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }

    @Test
    void testFindAll_shouldReturnAllTrainingTypes() {
        List<TrainingType> types = trainingTypeRepository.findAll();

        assertNotNull(types);
        assertEquals(7, types.size());
    }

    @Test
    void testFindByName_whenExists_shouldReturnTrainingType() {
        String name = "FITNESS";

        TrainingType type = trainingTypeRepository.findByName(name);

        assertNotNull(type);
        assertEquals(epam.gym.constants.TrainingType.FITNESS, type.getTrainingTypeName());
    }

    @Test
    void testFindByName_withDifferentTypes_shouldReturnCorrectTypes() {
        String[] expectedTypes = {"FITNESS", "YOGA", "CARDIO", "BOXING", "PILATES", "CROSSFIT", "SWIMMING"};

        for (String typeName : expectedTypes) {
            TrainingType type = trainingTypeRepository.findByName(typeName);

            assertNotNull(type, "Training type " + typeName + " should exist");
            assertEquals(epam.gym.constants.TrainingType.valueOf(typeName), type.getTrainingTypeName());
        }
    }

    @Test
    void testFindByName_whenNotExists_shouldReturnNull() {
        String nonExistentName = "NONEXISTENT";
        TrainingType type = trainingTypeRepository.findByName(nonExistentName);
        assertNull(type);
    }

    @Test
    void testFindById_whenExists_shouldReturnTrainingType() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        assertFalse(types.isEmpty());
        Long firstId = types.get(0).getId();

        TrainingType type = trainingTypeRepository.findById(firstId);

        assertNotNull(type);
        assertEquals(firstId, type.getId());
        assertNotNull(type.getTrainingTypeName());
    }

    @Test
    void testFindById_whenNotExists_shouldReturnNull() {
        TrainingType type = trainingTypeRepository.findById(999L);
        assertNull(type);
    }
}
