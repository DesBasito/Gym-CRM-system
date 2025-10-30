package epam.gym.services.interfaces;

import epam.gym.dao.impl.TraineeDao;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.entities.Trainee;

public interface TraineeService {
    Trainee create(TraineeDto traineeDto);
    Trainee update(TraineeDto traineeDto);
    Trainee select();
    void delete(String id);
}
