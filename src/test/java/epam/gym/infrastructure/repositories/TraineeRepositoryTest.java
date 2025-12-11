package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainee;
import jakarta.persistence.EntityManager;
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
class TraineeRepositoryTest {

    private TraineeRepository traineeRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Test
    void testFindAll_shouldReturnAllTrainees() {
        List<Trainee> trainees = traineeRepository.findAll(0,3);

        assertNotNull(trainees);
        assertEquals(3, trainees.size());
    }

    @Test
    void testFindById_whenTraineeExists_shouldReturnTrainee() {
        List<Trainee> trainees = traineeRepository.findAll(0,3);
        assertFalse(trainees.isEmpty());
        Long firstId = trainees.get(0).getId();

        Trainee trainee = traineeRepository.findById(firstId).orElse(null);

        assertNotNull(trainee);
        assertEquals(firstId, trainee.getId());
        assertNotNull(trainee.getUser());
        assertNotNull(trainee.getUser().getUsername());
    }

    @Test
    void testFindById_whenTraineeDoesNotExist_shouldReturnNull() {
        Trainee trainee = traineeRepository.findById(999L).orElse(null);

        assertNull(trainee);
    }

    @Test
    void testFindByUser_Username_whenExists_shouldReturnTrainee() {
        String username = "Alice.Brown";

        Trainee trainee = traineeRepository.findByUser_Username(username).orElse(null);

        assertNotNull(trainee);
        assertEquals(username, trainee.getUser().getUsername());
        assertNotNull(trainee.getDateOfBirth());
        assertNotNull(trainee.getAddress());
    }

    @Test
    void testFindByUser_Username_whenNotExists_shouldReturnEmpty() {
        String username = "NonExistent.User";

        Optional<Trainee> trainee = traineeRepository.findByUser_Username(username);

        assertTrue(trainee.isEmpty());
    }

    @Test
    void testChangePassword_shouldUpdatePassword() {
        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        assertNotNull(trainee);
        String newPassword = "newSecurePassword123";

        trainee.getUser().setPassword(newPassword);
        traineeRepository.save(trainee);

        boolean authenticated = traineeRepository.authenticate("Alice.Brown", newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testActivate_shouldSetActiveToTrue() {
        Trainee trainee = traineeRepository.findByUser_Username("Bob.Wilson").orElseThrow();
        assertNotNull(trainee);

        trainee.getUser().setIsActive(true);
        traineeRepository.save(trainee);

        Trainee activated = traineeRepository.findById(trainee.getId()).orElseThrow();
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_shouldSetActiveToFalse() {
        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        assertNotNull(trainee);
        assertTrue(trainee.getUser().getIsActive());

        trainee.getUser().setIsActive(false);
        traineeRepository.save(trainee);

        Trainee deactivated = traineeRepository.findById(trainee.getId()).orElseThrow();
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDeleteById_shouldRemoveTrainee() {
        Trainee trainee = traineeRepository.findByUser_Username("Alice.Brown").orElseThrow();
        assertNotNull(trainee);
        Long traineeId = trainee.getId();

        trainee.removeAssociations();
        entityManager.flush();
        traineeRepository.deleteById(traineeId);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> deleted = traineeRepository.findById(traineeId);
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testDeleteById_byUsername_shouldRemoveTrainee() {
        String username = "Bob.Wilson";
        Trainee trainee = traineeRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainee);

        trainee.removeAssociations();
        entityManager.flush();
        traineeRepository.deleteById(trainee.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Trainee> deleted = traineeRepository.findByUser_Username(username);
        assertTrue(deleted.isEmpty());
    }
}
