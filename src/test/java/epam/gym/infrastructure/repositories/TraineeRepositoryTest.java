package epam.gym.infrastructure.repositories;

import epam.gym.config.TestConfig;
import epam.gym.infrastructure.entities.Trainee;
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
class TraineeRepositoryTest {

    private TraineeRepository traineeRepository;

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Test
    void testFindAll_shouldReturnAllTrainees() {
        List<Trainee> trainees = traineeRepository.findAll();

        assertNotNull(trainees);
        assertEquals(3, trainees.size());
    }

    @Test
    void testFindById_whenTraineeExists_shouldReturnTrainee() {
        List<Trainee> trainees = traineeRepository.findAll();
        assertFalse(trainees.isEmpty());
        Long firstId = trainees.get(0).getId();

        Trainee trainee = traineeRepository.findById(firstId);

        assertNotNull(trainee);
        assertEquals(firstId, trainee.getId());
        assertNotNull(trainee.getUser());
        assertNotNull(trainee.getUser().getUsername());
    }

    @Test
    void testFindById_whenTraineeDoesNotExist_shouldReturnNull() {
        Trainee trainee = traineeRepository.findById(999L);

        assertNull(trainee);
    }

    @Test
    void testFindByUsername_whenExists_shouldReturnTrainee() {
        String username = "Alice.Brown";

        Trainee trainee = traineeRepository.findByUsername(username);

        assertNotNull(trainee);
        assertEquals(username, trainee.getUser().getUsername());
        assertNotNull(trainee.getDateOfBirth());
        assertNotNull(trainee.getAddress());
    }

    @Test
    void testFindByUsername_whenNotExists_shouldReturnNull() {
        String username = "NonExistent.User";

        Trainee trainee = traineeRepository.findByUsername(username);

        assertNull(trainee);
    }

    @Test
    void testAuthenticate_withCorrectCredentials_shouldReturnTrue() {
        String username = "Alice.Brown";
        String password = "mypass111";

        boolean result = traineeRepository.authenticate(username, password);

        assertTrue(result);
    }

    @Test
    void testAuthenticate_withIncorrectPassword_shouldReturnFalse() {
        String username = "Alice.Brown";
        String wrongPassword = "wrongpassword";

        boolean result = traineeRepository.authenticate(username, wrongPassword);

        assertFalse(result);
    }

    @Test
    void testAuthenticate_withNonExistentUser_shouldReturnFalse() {
        String username = "NonExistent.User";
        String password = "password";

        boolean result = traineeRepository.authenticate(username, password);

        assertFalse(result);
    }

    @Test
    void testChangePassword_byId_shouldUpdatePassword() {
        Trainee trainee = traineeRepository.findByUsername("Alice.Brown");
        assertNotNull(trainee);
        String newPassword = "newSecurePassword123";

        traineeRepository.changePassword(trainee.getId(), newPassword);

        boolean authenticated = traineeRepository.authenticate("Alice.Brown", newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testChangePassword_byUsername_shouldUpdatePassword() {
        String username = "Bob.Wilson";
        String newPassword = "anotherNewPassword456";

        traineeRepository.changePassword(username, newPassword);

        boolean authenticated = traineeRepository.authenticate(username, newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testActivate_byId_shouldSetActiveToTrue() {
        Trainee trainee = traineeRepository.findByUsername("Bob.Wilson");
        assertNotNull(trainee);

        traineeRepository.activate(trainee.getId());

        Trainee activated = traineeRepository.findById(trainee.getId());
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testActivate_byUsername_shouldSetActiveToTrue() {
        String username = "Bob.Wilson";

        traineeRepository.activate(username);

        Trainee activated = traineeRepository.findByUsername(username);
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_byId_shouldSetActiveToFalse() {
        Trainee trainee = traineeRepository.findByUsername("Alice.Brown");
        assertNotNull(trainee);
        assertTrue(trainee.getUser().getIsActive());

        traineeRepository.deactivate(trainee.getId());

        Trainee deactivated = traineeRepository.findById(trainee.getId());
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_byUsername_shouldSetActiveToFalse() {
        String username = "Charlie.Davis";

        traineeRepository.deactivate(username);

        Trainee deactivated = traineeRepository.findByUsername(username);
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDelete_byId_shouldRemoveTrainee() {
        Trainee trainee = traineeRepository.findByUsername("Alice.Brown");
        assertNotNull(trainee);
        Long traineeId = trainee.getId();

        traineeRepository.delete(traineeId);

        Trainee deleted = traineeRepository.findById(traineeId);
        assertNull(deleted);
    }

    @Test
    void testDelete_byUsername_shouldRemoveTrainee() {
        String username = "Bob.Wilson";
        Trainee trainee = traineeRepository.findByUsername(username);
        assertNotNull(trainee);

        traineeRepository.delete(trainee.getId());

        Trainee deleted = traineeRepository.findByUsername(username);
        assertNull(deleted);
    }

    @Test
    void testDelete_byId_whenNotExists_shouldThrowException() {
        Long nonExistentId = 999L;

        assertThrows(IllegalArgumentException.class, () -> {
            traineeRepository.delete(nonExistentId);
        });
    }

    @Test
    void testDelete_byUserId_whenNotExists_shouldThrowException() {
        Long nonExistentUseId = 234L;

        assertThrows(IllegalArgumentException.class, () -> {
            traineeRepository.delete(nonExistentUseId);
        });
    }
}
