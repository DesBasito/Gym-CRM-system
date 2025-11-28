package epam.gym.infrastructure.controller.interfaces;

import epam.gym.domain.dto.request.TrainerRequest;
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

@Tag(name = "Trainer Management", description = "Operations for trainer management")
public interface TrainerController {

    @PostMapping
    @Operation(summary = "Register new trainer", description = "Creates a new trainer profile and returns generated credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponse> registerTrainer(
            @Parameter(description = "Trainer registration data", required = true)
            @Valid @RequestBody TrainerRequest request);
}
