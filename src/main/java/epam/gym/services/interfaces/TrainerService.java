package epam.gym.services.interfaces;

import epam.gym.entities.Trainee;

public interface TrainerService {
    Trainee create();
    Trainee update();
    Trainee select();
}
