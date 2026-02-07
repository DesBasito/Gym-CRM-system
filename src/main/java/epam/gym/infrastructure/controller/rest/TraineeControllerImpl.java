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
import epam.gym.infrastructure.security.util.AuthenticatedUserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final AuthenticatedUserUtil authenticatedUserUtil;

    @Override
    public ResponseEntity<RegistrationResponse> registerTrainee(TraineeRequest request) {
        log.info("Register trainee request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponse response = traineeService.create(request);

        log.info("Trainee registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TraineeProfileDto> getTraineeProfile(Authentication authentication) {
        log.info("Get trainee profile request received for username: {}", authentication.getName());

        TraineeProfileDto profile = traineeService.selectByUsername(authentication.getName());

        log.info("Trainee profile retrieved successfully for username: {}", authentication.getName());
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
    public ResponseEntity<List<TrainerInfoDto>> getAvailableTrainers() {
        log.info("Get available trainers request received for trainee: {}", authenticatedUserUtil.getCurrentUsername());

        List<TrainerInfoDto> trainers = trainerService.findAllNotAssignedToTrainee(authenticatedUserUtil.getCurrentUsername());

        log.info("Found {} available trainers for trainee: {}", trainers.size(), authenticatedUserUtil.getCurrentUsername());
        return ResponseEntity.ok(trainers);
    }

    @Override
    @PreAuthorize("@authenticatedUserUtil.isProfileOwner(#id, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<TraineeProfileDto> updateTraineeProfile(UpdateTraineeRequest request, Long id) {
        log.info("Update trainee profile request received for username: {}", request.getUsername());

        TraineeProfileDto profile = traineeService.updateByUsername(request);

        log.info("Trainee profile updated successfully for username: {}", request.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        log.info("Change password request received for trainee: {}", authenticatedUserUtil.getCurrentUsername());

        traineeService.changePassword(authenticatedUserUtil.getCurrentUsername(), request.getOldPassword(), request.getNewPassword());

        log.info("Password changed successfully for trainee: {}", authenticatedUserUtil.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("@authenticatedUserUtil.isProfileOwnerByUsername(#username, authentication.name) or hasRole('ADMIN')")
    public ResponseEntity<Void> activateDeactivateTrainee(String username, Boolean isActive) {
        log.info("Activate/Deactivate trainee request received for username: {}, isActive: {}", username, isActive);

        traineeService.setActiveStatus(username, isActive);

        log.info("Trainee status updated successfully for username: {}, new status: {}", username, isActive);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainerInfoDto>> updateTrainersList(UpdateTraineeTrainersRequest request) {
        log.info("Update trainers list request received for trainee: {}", authenticatedUserUtil.getCurrentUsername());

        List<TrainerInfoDto> trainers = traineeService.updateTrainersList(
                authenticatedUserUtil.getCurrentUsername(),
                request.getTrainerUsernames()
        );

        log.info("Trainers list updated successfully for trainee: {}, trainers count: {}", authenticatedUserUtil.getCurrentUsername(), trainers.size());
        return ResponseEntity.ok(trainers);
    }
}
