package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.controller.interfaces.TrainerController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
@Slf4j
public class TrainerControllerImpl implements TrainerController {

    private final TrainerService trainerService;

    @Override
    public ResponseEntity<RegistrationResponse> registerTrainer(TrainerRequest request) {
        log.info("Register trainer request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponse response = trainerService.create(request);

        log.info("Trainer registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
