package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;

public interface TrainerService {
    TrainerModel create(TrainerRequest trainerDto);
    TrainerModel update(TrainerRequest trainerDto, String oldUsername);
    TrainerModel select(String username);
    void activate(String username);
    void deactivate(String username);
    void changePassword(String username, String newPassword);
}
