package epam.gym.infrastructure.controller.interfaces;

import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeTrainersRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.dto.response.TrainingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trainee Management", description = "Operations for trainee management")
public interface TraineeController {

    @PostMapping
    @Operation(summary = "Register new trainee",
            description = "Creates a new trainee profile and returns generated credentials",
    security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainee registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponse> registerTrainee(
            @Parameter(description = "Trainee registration data", required = true)
            @Valid @RequestBody TraineeRequest request);

    @GetMapping("/profile")
    @Operation(summary = "Get trainee profile", description = "Retrieves authenticated trainee profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authorized!")
    })
    ResponseEntity<TraineeProfileDto> getTraineeProfile(Authentication authentication);

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete trainee profile", description = "Deletes a trainee profile by username (hard delete with cascade)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<Void> deleteTraineeProfile(
            @Parameter(description = "Trainee username", required = true)
            @PathVariable("username") String username);

    @GetMapping("/available-trainers")
    @Operation(summary = "Get available trainers", description = "Retrieves active trainers not assigned to the trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available trainers retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<List<TrainerInfoDto>> getAvailableTrainers();

    @PutMapping("/{id}")
    @Operation(summary = "Update trainee profile", description = "Updates trainee profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<TraineeProfileDto> updateTraineeProfile(
            @Parameter(description = "Trainee update data", required = true)
            @Valid @RequestBody UpdateTraineeRequest request,
            @PathVariable("id") Long id);

    @PutMapping("/change-password")
    @Operation(summary = "Change trainee password", description = "Changes the password for a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid old password")
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody ChangePasswordRequest request);

    @PatchMapping
    @Operation(summary = "Activate/Deactivate trainee", description = "Activates or deactivates a trainee profile (non-idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainee status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee not found")
    })
    ResponseEntity<Void> activateDeactivateTrainee(
            @Parameter(description = "Trainee username", required = true)
            @RequestParam("username") String username,
            @Parameter(description = "Active status", required = true)
            @RequestParam("isActive") Boolean isActive);

    @PutMapping("/trainers")
    @Operation(summary = "Update trainee's trainer list", description = "Updates the list of trainers assigned to a trainee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainers list updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainee or trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<List<TrainerInfoDto>> updateTrainersList(
            @Parameter(description = "Update trainers list request", required = true)
            @Valid @RequestBody UpdateTraineeTrainersRequest request);
}
