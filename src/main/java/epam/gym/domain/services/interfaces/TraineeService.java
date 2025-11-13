package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;

public interface TraineeService {
    TraineeModel create(TraineeRequest traineeRequest);
    TraineeModel update(TraineeRequest traineeRequest, String username);
    TraineeModel select(String username);
    void delete(String username);
    void activate(String username);
    void deactivate(String username);
    void changePassword(String username, String newPassword);
}
