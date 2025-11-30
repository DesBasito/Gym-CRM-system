package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeTrainersRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.controller.interfaces.TraineeController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeControllerImpl implements TraineeController {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Override
    public ResponseEntity<RegistrationResponse> registerTrainee(TraineeRequest request) {
        log.info("Register trainee request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponse response = traineeService.create(request);

        log.info("Trainee registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TraineeProfileDto> getTraineeProfile(String username) {
        log.info("Get trainee profile request received for username: {}", username);

        TraineeProfileDto profile = traineeService.selectByUsername(username);

        log.info("Trainee profile retrieved successfully for username: {}", username);
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> deleteTraineeProfile(String username) {
        log.info("Delete trainee profile request received for username: {}", username);

        traineeService.delete(username);

        log.info("Trainee profile deleted successfully for username: {}", username);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerInfoDto>> getAvailableTrainers(String username) {
        log.info("Get available trainers request received for trainee: {}", username);

        List<TrainerInfoDto> trainers = trainerService.findAllNotAssignedToTrainee(username);

        log.info("Found {} available trainers for trainee: {}", trainers.size(), username);
        return ResponseEntity.ok(trainers);
    }

    @Override
    public ResponseEntity<TraineeProfileDto> updateTraineeProfile(UpdateTraineeRequest request) {
        log.info("Update trainee profile request received for username: {}", request.getUsername());

        TraineeProfileDto profile = traineeService.updateByUsername(request);

        log.info("Trainee profile updated successfully for username: {}", request.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        log.info("Change password request received for trainee: {}", request.getUsername());

        traineeService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());

        log.info("Password changed successfully for trainee: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> activateDeactivateTrainee(String username, Boolean isActive) {
        log.info("Activate/Deactivate trainee request received for username: {}, isActive: {}", username, isActive);

        traineeService.setActiveStatus(username, isActive);

        log.info("Trainee status updated successfully for username: {}, new status: {}", username, isActive);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerInfoDto>> updateTrainersList(UpdateTraineeTrainersRequest request) {
        log.info("Update trainers list request received for trainee: {}", request.getTraineeUsername());

        List<TrainerInfoDto> trainers = traineeService.updateTrainersList(
                request.getTraineeUsername(),
                request.getTrainerUsernames()
        );

        log.info("Trainers list updated successfully for trainee: {}, trainers count: {}", request.getTraineeUsername(), trainers.size());
        return ResponseEntity.ok(trainers);
    }
}
