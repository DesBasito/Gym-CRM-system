package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.UpdateTrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.controller.interfaces.TrainerController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerControllerImpl implements TrainerController {

    private final TrainerService trainerService;

    @Override
    public ResponseEntity<RegistrationResponse> registerTrainer(TrainerRequest request) {
        log.info("Register trainer request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponse response = trainerService.create(request);

        log.info("Trainer registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<TrainerProfileDto> getTrainerProfile(String username) {
        log.info("Get trainer profile request received for username: {}", username);

        TrainerProfileDto profile = trainerService.selectByUsername(username);

        log.info("Trainer profile retrieved successfully for username: {}", username);
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<TrainerProfileDto> updateTrainerProfile(UpdateTrainerRequest request) {
        log.info("Update trainer profile request received for username: {}", request.getUsername());

        TrainerProfileDto profile = trainerService.updateByUsername(request);

        log.info("Trainer profile updated successfully for username: {}", request.getUsername());
        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {
        log.info("Change password request received for trainer: {}", request.getUsername());

        trainerService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());

        log.info("Password changed successfully for trainer: {}", request.getUsername());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> activateDeactivateTrainer(String username, Boolean isActive) {
        log.info("Activate/Deactivate trainer request received for username: {}, isActive: {}", username, isActive);

        trainerService.setActiveStatus(username, isActive);

        log.info("Trainer status updated successfully for username: {}, new status: {}", username, isActive);
        return ResponseEntity.ok().build();
    }
}
