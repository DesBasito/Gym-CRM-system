package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.infrastructure.controller.interfaces.TraineeController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
@Slf4j
public class TraineeControllerImpl implements TraineeController {

    private final TraineeService traineeService;

    @Override
    public ResponseEntity<RegistrationResponse> registerTrainee(TraineeRequest request) {
        log.info("Register trainee request received: {} {}", request.getFirstName(), request.getLastName());

        RegistrationResponse response = traineeService.create(request);

        log.info("Trainee registered successfully: {}", response.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
