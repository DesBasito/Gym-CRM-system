package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.UpdateTrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.dto.response.TrainerInfoDto;

import java.util.List;

public interface TrainerService {
    RegistrationResponse create(TrainerRequest trainerDto);
    TrainerModel update(TrainerRequest trainerDto, Long id);
    TrainerProfileDto updateByUsername(UpdateTrainerRequest request);
    TrainerProfileDto selectByUsername(String username);
    void changePassword(String username, String oldPassword, String newPassword);
    List<TrainerInfoDto> findAllNotAssignedToTrainee(String traineeUsername);
    void delete(String username);
    void setActiveStatus(String username, Boolean isActive);
}
