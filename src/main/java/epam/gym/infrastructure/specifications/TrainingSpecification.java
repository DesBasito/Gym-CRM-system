package epam.gym.infrastructure.specifications;

import epam.gym.constants.TrainingType;
import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.infrastructure.entities.Training;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class TrainingSpecification {

    public static Specification<Training> filterTraineeTrainings(TraineeTrainingsFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getUsername() != null && !filterRequest.getUsername().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("trainee").get("user").get("username"),
                        filterRequest.getUsername()
                ));
            }

            if (filterRequest.getPeriodFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("trainingDate"),
                        filterRequest.getPeriodFrom()
                ));
            }

            if (filterRequest.getPeriodTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("trainingDate"),
                        filterRequest.getPeriodTo()
                ));
            }

            if (filterRequest.getTrainerName() != null && !filterRequest.getTrainerName().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("trainer").get("user").get("username"),
                        filterRequest.getTrainerName()
                ));
            }

            if (filterRequest.getTrainingType() != null && !filterRequest.getTrainingType().isBlank()) {
                TrainingType trainingType = TrainingType.valueOf(filterRequest.getTrainingType().toUpperCase());
                predicates.add(criteriaBuilder.equal(
                        root.get("trainingType").get("trainingTypeName"),
                        trainingType
                ));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Training> filterTrainerTrainings(TrainerTrainingsFilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filterRequest.getUsername() != null && !filterRequest.getUsername().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("trainer").get("user").get("username"),
                        filterRequest.getUsername()
                ));
            }

            if (filterRequest.getPeriodFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("trainingDate"),
                        filterRequest.getPeriodFrom()
                ));
            }

            if (filterRequest.getPeriodTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        root.get("trainingDate"),
                        filterRequest.getPeriodTo()
                ));
            }

            if (filterRequest.getTraineeName() != null && !filterRequest.getTraineeName().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("trainee").get("user").get("username"),
                        filterRequest.getTraineeName()
                ));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}