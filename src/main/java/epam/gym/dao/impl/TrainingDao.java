package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDao implements EntityDao<Training, EmbeddedTrainingId> {

    @Autowired
    private Map<EmbeddedTrainingId, Training> trainingStorage;

    @Override
    public Optional<Training> create(Training entity) {
        if (entity == null || entity.getTrainingId() == null) {
            return Optional.empty();
        }
        trainingStorage.put(entity.getTrainingId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Training> select(EmbeddedTrainingId id) {
        return Optional.ofNullable(trainingStorage.get(id));
    }
}