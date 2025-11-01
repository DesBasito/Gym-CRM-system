package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDao implements EntityDao<Trainer, String> {

    @Autowired
    private Map<String, Trainer> trainerStorage;

    @Override
    public Optional<Trainer> create(Trainer entity) {
        if (entity == null || entity.getUserId() == null) {
            return Optional.empty();
        }
        trainerStorage.put(entity.getUserId(), entity);
        return Optional.of(entity);
    }

    @Override
    public Optional<Trainer> select(String id) {
        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public Optional<Trainer> update(Trainer entity) {
        if (entity == null || entity.getUserId() == null || !trainerStorage.containsKey(entity.getUserId())) {
            return Optional.empty();
        }
        trainerStorage.put(entity.getUserId(), entity);
        return Optional.of(entity);
    }
}