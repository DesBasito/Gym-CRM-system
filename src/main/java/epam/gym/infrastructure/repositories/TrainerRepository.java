package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.util.List;

@Slf4j
@Repository
public class TrainerRepository extends epam.gym.infrastructure.repositories.BaseUserRepository<Trainer> {

    public TrainerRepository(EntityManager entityManager) {
        super(entityManager, Trainer.class);
    }

    public List<Trainer> findAllNotAssignedToTrainee(String traineeUsername) {
        if (traineeUsername == null) {
            log.warn("Attempt to find trainers with null trainee username");
            throw new IllegalArgumentException("Trainee username cannot be null");
        }

        Query query = entityManager.createNativeQuery(
                "SELECT t.*, u.* FROM trainers t " +
                "INNER JOIN users u ON t.user_id = u.id " +
                "WHERE t.id NOT IN " +
                "(SELECT tt.trainer_id FROM trainers_trainees tt " +
                "INNER JOIN trainees tr ON tt.trainee_id = tr.id " +
                "WHERE tr.user_id IN " +
                "(SELECT id FROM users WHERE username = :traineeUsername)) " +
                "AND u.is_active = true",
                Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);
        return query.getResultList();
    }
}