package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.models.TraineeModel;

import java.util.List;

public interface TraineeService {
    RegistrationResponse create(TraineeRequest traineeRequest);
    TraineeModel update(TraineeRequest traineeRequest, Long id);
    TraineeProfileDto updateByUsername(UpdateTraineeRequest request);
    TraineeProfileDto selectByUsername(String username);
    void delete(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    void setActiveStatus(String username, Boolean isActive);
    List<TrainerInfoDto> updateTrainersList(String traineeUsername, List<String> trainerUsernames);
}
