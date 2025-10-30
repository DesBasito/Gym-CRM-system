package epam.gym.services.impl;

import epam.gym.dao.impl.TraineeDao;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.entities.Trainee;
import epam.gym.services.interfaces.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {
    @Autowired
    private TraineeDao traineeDao;

    @Override
    public Trainee create(TraineeDto traineeDto) {
        return null;
    }

    @Override
    public Trainee update(TraineeDto traineeDto) {
        return null;
    }

    @Override
    public Trainee select() {
        return null;
    }

    @Override
    public void delete(String id) {
        traineeDao.delete(id);
    }
}
