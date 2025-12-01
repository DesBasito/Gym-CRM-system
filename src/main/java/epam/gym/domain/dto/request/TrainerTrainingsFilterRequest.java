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
@Schema(description = "Filter request for trainer trainings")
public class TrainerTrainingsFilterRequest {
    @Schema(description = "Trainer username", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jane.Smith")
    String username;

    @Schema(description = "Period from date", example = "2024-01-01")
    LocalDate periodFrom;

    @Schema(description = "Period to date", example = "2024-12-31")
    LocalDate periodTo;

    @Schema(description = "Trainee name to filter by", example = "John.Doe")
    String traineeName;
}