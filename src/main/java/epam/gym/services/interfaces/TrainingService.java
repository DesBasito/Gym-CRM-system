package epam.gym.services.interfaces;

import epam.gym.domain.entities.Trainee;

public interface TrainingService {
    Trainee create();
    Trainee select();
}
