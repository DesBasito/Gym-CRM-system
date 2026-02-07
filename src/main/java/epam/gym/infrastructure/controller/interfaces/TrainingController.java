package epam.gym.infrastructure.controller.interfaces;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Training Management", description = "Operations for training management")
public interface TrainingController {

    @GetMapping("/types")
    @Operation(summary = "Get Training types", description = "Retrieves list of all training types", security = {})
    @ApiResponse(responseCode = "200", description = "Types retrieved successfully")
    ResponseEntity<List<TrainingTypeDto>> getTrainingTypes();

    @PostMapping
    @Operation(summary = "Add training", description = "Creates a new training session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Training created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found")
    })
    ResponseEntity<Void> addTraining(
            @Parameter(description = "Training creation data", required = true)
            @Valid @RequestBody TrainingRequest request);

    @GetMapping("/trainee")
    @Operation(summary = "Get trainee trainings", description = "Retrieves list of trainings for a trainee with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainingDto>> getTraineeTrainings(
            @Parameter(description = "Filter criteria for trainee trainings", required = true)
            @Valid @ModelAttribute TraineeTrainingsFilterRequest filterRequest);

    @GetMapping("/trainer")
    @Operation(summary = "Get trainer trainings", description = "Retrieves list of trainings for a trainer with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainings retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<List<TrainingDto>> getTrainerTrainings(
            @Parameter(description = "Filter criteria for trainer trainings", required = true)
            @Valid @ModelAttribute TrainerTrainingsFilterRequest filterRequest);
}
