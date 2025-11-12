package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingTypeRepository {
    private final EntityManager entityManager;

    public TrainingType findByName(String name) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name",
                    TrainingType.class);
            query.setParameter("name", name);
            return (TrainingType) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("TrainingType not found with name: {}", name);
            return null;
        }
    }
}