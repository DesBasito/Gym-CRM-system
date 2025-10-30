package epam.gym.services.interfaces;

import epam.gym.domain.entities.Trainee;

public interface TrainerService {
    Trainee create();
    Trainee update();
    Trainee select();
}
