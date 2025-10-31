package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDao implements EntityDao<Trainer, String> {
    @Autowired
    private Map<String, Trainer> trainerStorage;


    @Override
    public Optional<Trainer> create(Trainer entity) {
        return Optional.ofNullable(trainerStorage.put(entity.getUserId(), entity));
    }

    @Override
    public Optional<Trainer> select(String id) {
        return Optional.of(trainerStorage.get(id));
    }

    @Override
    public Optional<Trainer> update(Trainer entity) {
        return Optional.ofNullable(trainerStorage.put(entity.getUserId(), entity));
    }
}
