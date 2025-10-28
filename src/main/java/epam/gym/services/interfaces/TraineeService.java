package epam.gym.services.interfaces;

import epam.gym.entities.Trainee;

public interface TraineeService {
    Trainee create();
    Trainee update();
    Trainee select();
    void delete();
}
