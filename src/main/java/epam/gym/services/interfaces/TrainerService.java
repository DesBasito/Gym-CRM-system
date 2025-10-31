package epam.gym.services.interfaces;

import epam.gym.domain.dto.TrainerDto;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.Trainer;

public interface TrainerService {
    Trainer create(TrainerDto trainerDto);
    Trainer update(TrainerDto trainerDto, String oldUsername);
    Trainer select(String id);
}
