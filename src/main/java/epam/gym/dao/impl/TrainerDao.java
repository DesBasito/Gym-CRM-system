package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TrainerDao implements EntityDao<Trainer, String> {
    @Autowired
    private Map<String, Trainer> trainerStorage;


    @Override
    public Trainer create(Trainer entity) {
        return trainerStorage.put(entity.getUserId(), entity);
    }

    @Override
    public List<Trainer> select() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public Trainer update(Trainer entity) {
        return trainerStorage.put(entity.getUserId(), entity);
    }
}
