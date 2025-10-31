package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainingDao implements EntityDao<Training, EmbeddedTrainingId> {
    @Autowired
    private Map<EmbeddedTrainingId,Training> trainingStorage;

    @Override
    public Optional<Training> create(Training entity) {
        return Optional.ofNullable(trainingStorage.put(entity.getTrainingId(), entity));
    }

    @Override
    public Optional<Training> select(EmbeddedTrainingId id) {
        return Optional.of(trainingStorage.get(id));
    }
}
