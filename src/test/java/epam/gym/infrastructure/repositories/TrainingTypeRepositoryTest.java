package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
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
        epam.gym.constants.TrainingType tType = epam.gym.constants.TrainingType.valueOf("FITNESS");
        TrainingType type = trainingTypeRepository.findTrainingTypeByTrainingTypeName(tType).orElse(null);

        assertNotNull(type);
        assertEquals(epam.gym.constants.TrainingType.FITNESS, type.getTrainingTypeName());
    }

    @Test
    void testCountTheActualSize_whenExists_shouldReturnSizeOfTrainingType() {
        Long type = trainingTypeRepository.count();

        assertNotNull(type);
        assertEquals(7, type);
    }

    @Test
    void testFindByName_withDifferentTypes_shouldReturnCorrectTypes() {
        String[] expectedTypes = {"FITNESS", "YOGA", "CARDIO", "BOXING", "PILATES", "CROSSFIT", "SWIMMING"};

        for (String typeName : expectedTypes) {
            epam.gym.constants.TrainingType trType = epam.gym.constants.TrainingType.valueOf(typeName);
            TrainingType type = trainingTypeRepository.findTrainingTypeByTrainingTypeName(trType).orElse(null);

            assertNotNull(type, "Training type " + typeName + " should exist");
            assertEquals(epam.gym.constants.TrainingType.valueOf(typeName), type.getTrainingTypeName());
        }
    }

    @Test
    void testFindAll_shouldReturnAllTypes() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        assertNotNull(types);
        assertEquals(7, types.size());
    }

    @Test
    void testFindById_whenExists_shouldReturnTrainingType() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        assertFalse(types.isEmpty());
        Long firstId = types.get(0).getId();

        TrainingType type = trainingTypeRepository.findById(firstId).orElse(null);

        assertNotNull(type);
        assertEquals(firstId, type.getId());
        assertNotNull(type.getTrainingTypeName());
    }

    @Test
    void testFindById_whenNotExists_shouldReturnEmpty() {
        Optional<TrainingType> type = trainingTypeRepository.findById(999L);
        assertTrue(type.isEmpty());
    }
}
