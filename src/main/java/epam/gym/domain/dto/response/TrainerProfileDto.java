package epam.gym.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Trainer profile details")
public class TrainerProfileDto {
    @Schema(description = "Trainer ID", example = "2L")
    Long id;

    @Schema(description = "Username", example = "John.Doe")
    String username;

    @Schema(description = "First name", example = "Jane")
    String firstName;

    @Schema(description = "Last name", example = "Smith")
    String lastName;

    @Schema(description = "Training specialization", example = "YOGA")
    String specialization;

    @Schema(description = "Is active", example = "true")
    Boolean isActive;

    @Schema(description = "List of trainees")
    List<TraineeInfoDto> trainees;
}