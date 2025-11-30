package epam.gym.infrastructure.repositories;

import epam.gym.config.TestConfig;
import epam.gym.infrastructure.entities.Trainer;
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
class TrainerRepositoryTest {

    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }


    @Test
    void testFindAll_shouldReturnAllTrainers() {
        List<Trainer> trainers = trainerRepository.findAll(0,4);

        assertNotNull(trainers);
        assertEquals(4, trainers.size());
    }

    @Test
    void testFindById_whenTrainerExists_shouldReturnTrainer() {
        List<Trainer> trainers = trainerRepository.findAll(0,4);
        assertFalse(trainers.isEmpty());
        Long firstId = trainers.get(0).getId();

        Trainer trainer = trainerRepository.findById(firstId);

        assertNotNull(trainer);
        assertEquals(firstId, trainer.getId());
        assertNotNull(trainer.getUser());
        assertNotNull(trainer.getUser().getUsername());
    }

    @Test
    void testFindById_whenTrainerDoesNotExist_shouldReturnNull() {
        Trainer trainer = trainerRepository.findById(999L);

        assertNull(trainer);
    }

    @Test
    void testFindByUsername_whenExists_shouldReturnTrainer() {
        String username = "John.Doe";

        Trainer trainer = trainerRepository.findByUsername(username);

        assertNotNull(trainer);
        assertEquals(username, trainer.getUser().getUsername());
        assertNotNull(trainer.getSpecialization());
    }

    @Test
    void testFindByUsername_whenNotExists_shouldReturnNull() {
        String username = "NonExistent.User";

        Trainer trainer = trainerRepository.findByUsername(username);

        assertNull(trainer);
    }

    @Test
    void testAuthenticate_withCorrectCredentials_shouldReturnTrue() {
        String username = "John.Doe";
        String password = "password123";

        boolean result = trainerRepository.authenticate(username, password);

        assertTrue(result);
    }

    @Test
    void testAuthenticate_withIncorrectPassword_shouldReturnFalse() {
        String username = "John.Doe";
        String wrongPassword = "wrongpassword";

        boolean result = trainerRepository.authenticate(username, wrongPassword);

        assertFalse(result);
    }

    @Test
    void testAuthenticate_withNonExistentUser_shouldReturnFalse() {
        String username = "NonExistent.User";
        String password = "password";

        boolean result = trainerRepository.authenticate(username, password);

        assertFalse(result);
    }

    @Test
    void testChangePassword_byId_shouldUpdatePassword() {
        Trainer trainer = trainerRepository.findByUsername("John.Doe");
        assertNotNull(trainer);
        String newPassword = "newSecurePassword123";

        trainerRepository.changePassword(trainer.getId(), newPassword);

        boolean authenticated = trainerRepository.authenticate("John.Doe", newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testChangePassword_byUsername_shouldUpdatePassword() {
        String username = "Jane.Smith";
        String newPassword = "anotherNewPassword456";

        trainerRepository.changePassword(username, newPassword);

        boolean authenticated = trainerRepository.authenticate(username, newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testActivate_byId_shouldSetActiveToTrue() {
        Trainer trainer = trainerRepository.findByUsername("John.Doe");
        assertNotNull(trainer);
        trainerRepository.deactivate(trainer.getId());

        trainerRepository.activate(trainer.getId());

        Trainer activated = trainerRepository.findById(trainer.getId());
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testActivate_byUsername_shouldSetActiveToTrue() {
        String username = "Jane.Smith";
        trainerRepository.deactivate(username);

        trainerRepository.activate(username);

        Trainer activated = trainerRepository.findByUsername(username);
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_byId_shouldSetActiveToFalse() {
        Trainer trainer = trainerRepository.findByUsername("Mike.Johnson");
        assertNotNull(trainer);
        assertTrue(trainer.getUser().getIsActive());

        trainerRepository.deactivate(trainer.getId());

        Trainer deactivated = trainerRepository.findById(trainer.getId());
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_byUsername_shouldSetActiveToFalse() {
        String username = "Sarah.Connor";

        trainerRepository.deactivate(username);

        Trainer deactivated = trainerRepository.findByUsername(username);
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDelete_byId_shouldRemoveTrainer() {
        Trainer trainer = trainerRepository.findByUsername("John.Doe");
        assertNotNull(trainer);
        Long trainerId = trainer.getId();

        trainerRepository.delete(trainerId);

        Trainer deleted = trainerRepository.findById(trainerId);
        assertNull(deleted);
    }

    @Test
    void testDelete_byUsername_shouldRemoveTrainer() {
        String username = "Jane.Smith";
        Trainer trainer = trainerRepository.findByUsername(username);
        assertNotNull(trainer);

        trainerRepository.delete(trainer.getId());

        Trainer deleted = trainerRepository.findByUsername(username);
        assertNull(deleted);
    }

    @Test
    void testDelete_byId_whenNotExists_shouldThrowException() {
        Long nonExistentId = 999L;

        assertThrows(IllegalArgumentException.class, () -> {
            trainerRepository.delete(nonExistentId);
        });
    }

    @Test
    void testDelete_byUsername_whenNotExists_shouldThrowException() {
        Long nonExistentUserId = 1234L;

        assertThrows(IllegalArgumentException.class, () -> {
            trainerRepository.delete(nonExistentUserId);
        });
    }

    @Test
    void testFindAllNotAssignedToTrainee_shouldReturnUnassignedTrainers() {
        String traineeUsername = "Alice.Brown";

        List<Trainer> unassigned = trainerRepository.findAllNotAssignedToTrainee(traineeUsername);

        assertNotNull(unassigned);
        assertTrue(unassigned.size() >= 2);

        boolean hasJohnDoe = unassigned.stream()
                .anyMatch(t -> "John.Doe".equals(t.getUser().getUsername()));
        boolean hasMikeJohnson = unassigned.stream()
                .anyMatch(t -> "Mike.Johnson".equals(t.getUser().getUsername()));

        assertTrue(hasJohnDoe);
        assertTrue(hasMikeJohnson);
    }
}
