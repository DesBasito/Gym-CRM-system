package epam.gym.infrastructure.repositories;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.infrastructure.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
            SELECT DISTINCT t FROM Training t
            WHERE (:username IS NULL OR t.trainee.user.username = :username)
            AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom)
            AND (:periodTo IS NULL OR t.trainingDate <= :periodTo)
            AND (:trainerName IS NULL OR t.trainer.user.username = :trainerName)
            AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)
            """)
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("trainerName") String trainerName,
            @Param("trainingType") epam.gym.constants.TrainingType trainingType
    );

    @Query("""
            SELECT DISTINCT t FROM Training t
            WHERE (:username IS NULL OR t.trainer.user.username = :username)
            AND (:periodFrom IS NULL OR t.trainingDate >= :periodFrom)
            AND (:periodTo IS NULL OR t.trainingDate <= :periodTo)
            AND (:traineeName IS NULL OR t.trainee.user.username = :traineeName)
            """)
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("periodFrom") LocalDate periodFrom,
            @Param("periodTo") LocalDate periodTo,
            @Param("traineeName") String traineeName
    );

    default List<Training> findTraineeTrainings(TraineeTrainingsFilterRequest filterRequest) {
        epam.gym.constants.TrainingType trainingType = null;
        if (filterRequest.getTrainingType() != null && !filterRequest.getTrainingType().isBlank()) {
            try {
                trainingType = epam.gym.constants.TrainingType.valueOf(filterRequest.getTrainingType().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid training type, will be treated as null
            }
        }
        return findTraineeTrainings(
                filterRequest.getUsername(),
                filterRequest.getPeriodFrom(),
                filterRequest.getPeriodTo(),
                filterRequest.getTrainerName(),
                trainingType
        );
    }

    default List<Training> findTrainerTrainings(TrainerTrainingsFilterRequest filterRequest) {
        return findTrainerTrainings(
                filterRequest.getUsername(),
                filterRequest.getPeriodFrom(),
                filterRequest.getPeriodTo(),
                filterRequest.getTraineeName()
        );
    }
}