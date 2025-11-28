package epam.gym.infrastructure.controller.interfaces;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Trainee Management", description = "Operations for trainee management")
public interface TraineeController {

    @PostMapping
    @Operation(summary = "Register new trainee", description = "Creates a new trainee profile and returns generated credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponse> registerTrainee(
            @Parameter(description = "Trainee registration data", required = true)
            @Valid @RequestBody TraineeRequest request);
}
