package epam.gym.infrastructure.repositories;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingRepository {

    private final EntityManager entityManager;

    public Training save(Training training) {
        if (training.getId() == null) {
            entityManager.persist(training);
            log.info("Training created: {}", training.getTrainingName());
        } else {
            training = entityManager.merge(training);
            log.info("Training updated: {}", training.getTrainingName());
        }
        return training;
    }

    public List<Training> findTraineeTrainings(TraineeTrainingsFilterRequest filterRequest) {
        return findTrainingsByParams(
                "trainee",
                filterRequest.getUsername(),
                filterRequest.getPeriodFrom(),
                filterRequest.getPeriodTo(),
                "trainer",
                filterRequest.getTrainerName(),
                filterRequest.getTrainingType()
        );
    }

    public List<Training> findTrainerTrainings(TrainerTrainingsFilterRequest filterRequest) {
        return findTrainingsByParams(
                "trainer",
                filterRequest.getUsername(),
                filterRequest.getPeriodFrom(),
                filterRequest.getPeriodTo(),
                "trainee",
                filterRequest.getTraineeName(),
                null
        );
    }

    private List<Training> findTrainingsByParams(
            String mainRole,
            String mainUsername,
            LocalDate periodFrom,
            LocalDate periodTo,
            String otherRole,
            String otherUsername,
            String trainingTypeName
    ) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        Join<Training, ?> mainJoin = training.join(mainRole);
        Join<Training, ?> otherJoin = null;
        if (otherRole != null && !otherRole.isBlank()) {
            otherJoin = training.join(otherRole);
        }

        List<Predicate> predicates = new ArrayList<>();

        if (mainUsername != null && !mainUsername.isBlank()) {
            predicates.add(cb.equal(mainJoin.get("user").get("username"), mainUsername));
        }

        if (periodFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), periodFrom));
        }
        if (periodTo != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), periodTo));
        }

        if (otherJoin != null && otherUsername != null && !otherUsername.isBlank()) {
            predicates.add(cb.equal(otherJoin.get("user").get("username"), otherUsername));
        }

        if (trainingTypeName != null && !trainingTypeName.isBlank()) {
            predicates.add(cb.equal(training.get("trainingType").get("trainingTypeName"), trainingTypeName));
        }

        query.select(training).distinct(true).where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

}