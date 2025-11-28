package epam.gym.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Training creation request")
public class TrainingRequest {
    @NotBlank(message = "Trainer username is required")
    @Schema(description = "Trainer username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jane.Smith")
    String trainerUsername;

    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", requiredMode = Schema.RequiredMode.REQUIRED, example = "John.Doe")
    String traineeUsername;

    @NotBlank(message = "Training name is required")
    @Schema(description = "Training name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Morning Yoga Session")
    String trainingName;

    @Schema(description = "Training type", example = "YOGA")
    String trainingType;

    @NotNull(message = "Training date is required")
    @Schema(description = "Training date", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-01-15")
    LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Schema(description = "Training duration in minutes", requiredMode = Schema.RequiredMode.REQUIRED, example = "60")
    Integer trainingDuration;
}
