package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {

    Optional<TrainingType> findByTrainingTypeName(epam.gym.constants.TrainingType trainingTypeName);

    default TrainingType findByName(String name) {
        try {
            epam.gym.constants.TrainingType enumValue = epam.gym.constants.TrainingType.valueOf(name.toUpperCase());
            return findByTrainingTypeName(enumValue).orElse(null);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}