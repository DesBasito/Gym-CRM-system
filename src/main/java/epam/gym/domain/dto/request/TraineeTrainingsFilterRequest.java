package epam.gym.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Filter request for trainee trainings")
public class TraineeTrainingsFilterRequest {
    @Schema(description = "Trainee username", requiredMode = Schema.RequiredMode.REQUIRED, example = "John.Doe")
    String username;

    @Schema(description = "Period from date", example = "2024-01-01")
    LocalDate periodFrom;

    @Schema(description = "Period to date", example = "2024-12-31")
    LocalDate periodTo;

    @Schema(description = "Trainer name to filter by", example = "Jane.Smith")
    String trainerName;

    @Schema(description = "Training type to filter by", example = "FITNESS")
    String trainingType;
}