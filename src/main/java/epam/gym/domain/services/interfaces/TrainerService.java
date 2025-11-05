package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainerCreationRequest;
import epam.gym.domain.entities.Trainer;

public interface TrainerService {
    Trainer create(TrainerCreationRequest trainerDto);
    Trainer update(TrainerCreationRequest trainerDto, String oldUsername);
    Trainer select(String id);
}
