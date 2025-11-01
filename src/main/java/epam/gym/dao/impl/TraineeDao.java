package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDao implements EntityDao<Trainee, String> {

    @Autowired
    private Map<String, Trainee> traineeStorage;

    @Override
    public Optional<Trainee> create(Trainee entity) {
        if (entity == null || entity.getUserId() == null) {
            return Optional.empty();
        }
        traineeStorage.put(entity.getUserId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Trainee> select(String id) {
        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public Optional<Trainee> update(Trainee entity) {
        if (entity == null || entity.getUserId() == null || !traineeStorage.containsKey(entity.getUserId())) {
            return Optional.empty();
        }
        traineeStorage.put(entity.getUserId(), entity);
        return Optional.of(entity);
    }

    @Override
    public boolean delete(String id) {
        return traineeStorage.remove(id) != null;
    }
}