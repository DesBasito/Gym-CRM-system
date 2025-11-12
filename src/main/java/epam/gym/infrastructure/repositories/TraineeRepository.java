package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;

@Slf4j
@Repository
public class TraineeRepository extends BaseUserRepository<Trainee> {

    public TraineeRepository(EntityManager entityManager) {
        super(entityManager, Trainee.class);
    }
}