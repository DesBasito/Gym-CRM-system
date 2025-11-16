package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;

public interface TraineeService {
    TraineeModel create(TraineeRequest traineeRequest);
    TraineeModel update(TraineeRequest traineeRequest, Long id);
    TraineeModel select(Long id);
    void delete(Long id);
    void activate(Long id);
    void deactivate(Long id);
    void changePassword(Long id, String newPassword);
}
