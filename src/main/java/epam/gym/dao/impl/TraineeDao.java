package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class TraineeDao implements EntityDao<Trainee, String> {

    @Autowired
    private Map<String, Trainee> traineeStorage;


    @Override
    public Trainee create(Trainee entity) {
        return traineeStorage.put(entity.getUserId(), entity);
    }

    @Override
    public List<Trainee> select() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public Trainee update(Trainee entity) {
        return traineeStorage.put(entity.getUserId(), entity);
    }

    @Override
    public void delete(String s) {
        traineeStorage.remove(s);
    }
}
