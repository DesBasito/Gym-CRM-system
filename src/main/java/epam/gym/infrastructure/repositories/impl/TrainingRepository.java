package epam.gym.infrastructure.repositories.impl;

import epam.gym.infrastructure.dao.EmbeddedTrainingDaoId;
import epam.gym.infrastructure.dao.TrainingDao;
import epam.gym.infrastructure.repositories.EntityRepository;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import epam.gym.infrastructure.mappers.TrainingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingRepository implements EntityRepository<Training, EmbeddedTrainingId> {
    private final Map<EmbeddedTrainingDaoId, TrainingDao> trainingStorage;
    private final TrainingMapper trainerMapper;

    @Override
    public Training save(Training entity) {
        if (entity == null) {
            log.warn("Attempt to save null training");
            throw new IllegalArgumentException("Attempt to save null training");
        }

        validate(entity);
        TrainingDao trainingDao = trainerMapper.toDao(entity);
        trainingStorage.put(trainingDao.getTrainingDaoId(), trainingDao);
        return entity;
    }

    @Override
    public Training select(EmbeddedTrainingId id) {
        if (id == null) {
            log.warn("Attempt to select training with null id");
            throw new IllegalArgumentException("Attempt to select training with null id");
        }

        EmbeddedTrainingDaoId daoId = new EmbeddedTrainingDaoId();
        daoId.setTraineeId(id.getTraineeId());
        daoId.setTrainerId(id.getTrainerId());
        daoId.setTrainingName(id.getTrainingName());

        TrainingDao trainingDao = trainingStorage.get(daoId);
        if (trainingDao == null) {
            log.warn("Training with id {} not found", id);
            throw new NoSuchElementException("Training with id "+id+" not found");
        }

        return trainerMapper.toModel(trainingDao);
    }

    private void validate(Training entity) {
        if (entity.getTrainingId() == null) {
            log.error("Training ID cannot be null");
            throw new IllegalArgumentException("Training ID cannot be null");
        }

        if (entity.getTrainingId().getTraineeId() == null) {
            log.error("Trainee ID cannot be null");
            throw new IllegalArgumentException("Trainee ID cannot be null");
        }

        if (entity.getTrainingId().getTrainerId() == null) {
            log.error("Trainer ID cannot be null");
            throw new IllegalArgumentException("Trainer ID cannot be null");
        }

        if (entity.getTrainingId().getTrainingName() == null) {
            log.error("Training name cannot be null");
            throw new IllegalArgumentException("Training name cannot be null");
        }
    }
}