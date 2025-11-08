package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TraineeCreationRequest;
import epam.gym.domain.entities.Trainee;

public interface TraineeService {
    Trainee create(TraineeCreationRequest traineeCreationRequest);
    Trainee update(TraineeCreationRequest traineeCreationRequest, String username);
    Trainee select(String id);
    void delete(String id);
}
