package epam.gym.infrastructure.repositories;

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
import java.util.function.Function;

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

    public List<Training> findTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate) {
        return buildTrainingsQuery(training -> {
            Join<Training, Trainee> trainee = training.join("trainee");
            return trainee.get("username");
        }, traineeUsername, fromDate, toDate);
    }

    public List<Training> findTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate) {
        return buildTrainingsQuery(training -> {
            Join<Training, Trainer> trainer = training.join("trainer");
            return trainer.get("username");
        }, trainerUsername, fromDate, toDate);
    }

    private List<Training> buildTrainingsQuery(Function<Root<Training>, Expression<String>> usernameGetter,
                                               String username, LocalDate fromDate, LocalDate toDate) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> training = query.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(usernameGetter.apply(training), username));
        addDatePredicates(cb, training, fromDate, toDate, predicates);

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        return entityManager.createQuery(query).getResultList();
    }

    private void addDatePredicates(CriteriaBuilder cb, Root<Training> training,
                                   LocalDate fromDate, LocalDate toDate, List<Predicate> predicates) {
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }
        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
    }
}