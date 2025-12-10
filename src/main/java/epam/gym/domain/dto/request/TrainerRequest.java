package epam.gym.domain.dto.request;

import epam.gym.infrastructure.validations.IsTrainingTypeValid;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Trainer request for create and update")
public class TrainerRequest {
    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Jane")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Smith")
    String lastName;

    @NotBlank(message = "Specialization is required")
    @IsTrainingTypeValid
    @Schema(description = "Training specialization", requiredMode = Schema.RequiredMode.REQUIRED, example = "YOGA")
    String specialization;
}
