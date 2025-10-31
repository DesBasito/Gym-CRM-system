package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDao implements EntityDao<Trainee, String> {

    @Autowired
    private Map<String, Trainee> traineeStorage;


    @Override
    public Optional<Trainee> create(Trainee entity) {
        return Optional.ofNullable(traineeStorage.put(entity.getUserId(), entity));
    }

    @Override
    public Optional<Trainee> select(String id) {
        return Optional.of(traineeStorage.get(id));
    }

    @Override
    public Optional<Trainee> update(Trainee entity) {
        return Optional.ofNullable(traineeStorage.put(entity.getUserId(), entity));
    }

    @Override
    public boolean delete(String s) {
        traineeStorage.remove(s);
        return traineeStorage.get(s) == null;
    }
}
