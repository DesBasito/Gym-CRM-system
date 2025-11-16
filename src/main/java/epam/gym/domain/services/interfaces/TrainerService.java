package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;

import java.util.List;

public interface TrainerService {
    TrainerModel create(TrainerRequest trainerDto);
    TrainerModel update(TrainerRequest trainerDto, Long id);
    TrainerModel select(Long id);
    void activate(Long id);
    void deactivate(Long id);
    void changePassword(Long id, String newPassword);
    List<TrainerModel> findAllNotAssignedToTrainee(String traineeUsername);

    void delete(Long trainerId);
}
