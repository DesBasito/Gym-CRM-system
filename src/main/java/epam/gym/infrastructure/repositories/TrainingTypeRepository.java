package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findTrainingTypeByTrainingTypeName(epam.gym.constants.TrainingType trainingTypeName);

}