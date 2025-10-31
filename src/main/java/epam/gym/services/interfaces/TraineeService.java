package epam.gym.services.interfaces;

import epam.gym.dao.impl.TraineeDao;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.entities.Trainee;

public interface TraineeService {
    Trainee create(TraineeDto traineeDto);
    Trainee update(TraineeDto traineeDto, String username);
    Trainee select(String id);
    void delete(String id);
}
