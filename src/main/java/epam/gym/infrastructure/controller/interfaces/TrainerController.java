package epam.gym.infrastructure.controller.interfaces;

import epam.gym.domain.dto.request.ChangePasswordRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.UpdateTrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TrainerProfileDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainer Management", description = "Operations for trainer management")
public interface TrainerController {

    @PostMapping
    @Operation(summary = "Register new trainer",
            description = "Creates a new trainer profile and returns generated credentials",
            security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Trainer registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<RegistrationResponse> registerTrainer(
            @Parameter(description = "Trainer registration data", required = true)
            @Valid @RequestBody TrainerRequest request);

    @GetMapping("/profile")
    @Operation(summary = "Get trainer profile", description = "Retrieves trainer profile information by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<TrainerProfileDto> getTrainerProfile();

    @PutMapping("/{id}")
    @Operation(summary = "Update trainer profile", description = "Updates trainer profile information (specialization is read-only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer profile updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<TrainerProfileDto> updateTrainerProfile(
            @Parameter(description = "Trainer update data", required = true)
            @Valid @RequestBody UpdateTrainerRequest request,
            @PathVariable(name = "id") Long id);

    @PutMapping("/change-password")
    @Operation(summary = "Change trainer password", description = "Changes the password for a trainer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Invalid old password")
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody ChangePasswordRequest request);

    @PatchMapping
    @Operation(summary = "Activate/Deactivate trainer", description = "Activates or deactivates a trainer profile (non-idempotent operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Trainer status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Trainer not found")
    })
    ResponseEntity<Void> activateDeactivateTrainer(
            @Parameter(description = "Trainer username", required = true)
            @RequestParam("username") String username,
            @Parameter(description = "Active status", required = true)
            @RequestParam("isActive") Boolean isActive);
}
