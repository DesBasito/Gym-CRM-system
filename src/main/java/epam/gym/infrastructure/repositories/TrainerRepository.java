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

        Query query = entityManager.createQuery(
                """
                SELECT DISTINCT tr FROM Trainer tr
                LEFT JOIN tr.trainees t
                LEFT JOIN t.user tu
                WHERE tr.user.isActive = true
                AND (tu.username IS NULL OR tu.username != :traineeUsername)
                """,
                Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);
        return query.getResultList();
    }
}