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
            epam.gym.constants.TrainingType enumValue = epam.gym.constants.TrainingType.valueOf(name.toUpperCase());
            Query query = entityManager.createQuery(
                    "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name",
                    TrainingType.class);
            query.setParameter("name", enumValue);
            return (TrainingType) query.getSingleResult();
        } catch (IllegalArgumentException e) {
            log.warn("Invalid TrainingType name: {}", name);
            return null;
        } catch (NoResultException e) {
            log.warn("TrainingType not found with name: {}", name);
            return null;
        }
    }

    public TrainingType findById(Long id) {
        try {
            return entityManager.find(TrainingType.class, id);
        } catch (NoResultException e) {
            log.warn("TrainingType not found with id: {}", id);
            return null;
        }
    }

    public List<TrainingType> findAll() {
        Query query = entityManager.createQuery(
                "SELECT t FROM " + TrainingType.class.getSimpleName() + " t",
                TrainingType.class);
        return query.getResultList();
    }

    public long count() {
        Query query = entityManager.createQuery(
                "SELECT COUNT(t) FROM " + TrainingType.class.getSimpleName() + " t",
                Long.class);
        return (Long) query.getSingleResult();
    }
}