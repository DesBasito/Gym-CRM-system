package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainer;
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
class TrainerRepositoryTest {

    private TrainerRepository trainerRepository;

    @Autowired
    private EntityManager entityManager;

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

        Trainer trainer = trainerRepository.findById(firstId).orElse(null);

        assertNotNull(trainer);
        assertEquals(firstId, trainer.getId());
        assertNotNull(trainer.getUser());
        assertNotNull(trainer.getUser().getUsername());
    }

    @Test
    void testFindById_whenTrainerDoesNotExist_shouldReturnEmpty() {
        Optional<Trainer> trainer = trainerRepository.findById(999L);

        assertTrue(trainer.isEmpty());
    }

    @Test
    void testFindByUser_Username_whenExists_shouldReturnTrainer() {
        String username = "John.Doe";

        Trainer trainer = trainerRepository.findByUser_Username(username).orElse(null);

        assertNotNull(trainer);
        assertEquals(username, trainer.getUser().getUsername());
        assertNotNull(trainer.getSpecialization());
    }

    @Test
    void testFindByUser_Username_whenNotExists_shouldReturnEmpty() {
        String username = "NonExistent.User";

        Optional<Trainer> trainer = trainerRepository.findByUser_Username(username);

        assertTrue(trainer.isEmpty());
    }

    @Test
    void testChangePassword_shouldUpdatePassword() {
        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        assertNotNull(trainer);
        String newPassword = "newSecurePassword123";

        trainer.getUser().setPassword(newPassword);
        trainerRepository.save(trainer);

        boolean authenticated = trainerRepository.authenticate("John.Doe", newPassword);
        assertTrue(authenticated);
    }

    @Test
    void testActivate_shouldSetActiveToTrue() {
        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        assertNotNull(trainer);
        trainer.getUser().setIsActive(false);
        trainerRepository.save(trainer);

        trainer.getUser().setIsActive(true);
        trainerRepository.save(trainer);

        Trainer activated = trainerRepository.findById(trainer.getId()).orElseThrow();
        assertTrue(activated.getUser().getIsActive());
    }

    @Test
    void testDeactivate_shouldSetActiveToFalse() {
        Trainer trainer = trainerRepository.findByUser_Username("Mike.Johnson").orElseThrow();
        assertNotNull(trainer);
        assertTrue(trainer.getUser().getIsActive());

        trainer.getUser().setIsActive(false);
        trainerRepository.save(trainer);

        Trainer deactivated = trainerRepository.findById(trainer.getId()).orElseThrow();
        assertFalse(deactivated.getUser().getIsActive());
    }

    @Test
    void testDeleteById_shouldRemoveTrainer() {
        Trainer trainer = trainerRepository.findByUser_Username("John.Doe").orElseThrow();
        assertNotNull(trainer);
        Long trainerId = trainer.getId();

        trainer.removeAssociations();
        entityManager.flush();
        trainerRepository.deleteById(trainerId);
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> deleted = trainerRepository.findById(trainerId);
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testDeleteById_byUsername_shouldRemoveTrainer() {
        String username = "Jane.Smith";
        Trainer trainer = trainerRepository.findByUser_Username(username).orElseThrow();
        assertNotNull(trainer);

        trainer.removeAssociations();
        entityManager.flush();
        trainerRepository.deleteById(trainer.getId());
        entityManager.flush();
        entityManager.clear();

        Optional<Trainer> deleted = trainerRepository.findByUser_Username(username);
        assertTrue(deleted.isEmpty());
    }

    @Test
    void testFindAllNotAssignedToTrainee_shouldReturnUnassignedTrainers() {
        String traineeUsername = "Alice.Brown";

        List<Trainer> unassigned = trainerRepository.findAllNotAssignedToTrainee(traineeUsername);

        assertNotNull(unassigned);
        assertTrue(unassigned.size() >= 2);

        boolean hasJaneSmith = unassigned.stream()
                .anyMatch(t -> "Jane.Smith".equals(t.getUser().getUsername()));
        boolean hasSarahConnor = unassigned.stream()
                .anyMatch(t -> "Sarah.Connor".equals(t.getUser().getUsername()));

        assertTrue(hasJaneSmith);
        assertTrue(hasSarahConnor);
    }
}
