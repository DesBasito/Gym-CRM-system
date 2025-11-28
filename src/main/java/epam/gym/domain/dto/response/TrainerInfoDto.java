package epam.gym.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Trainer basic information")
public class TrainerInfoDto {
    @Schema(description = "Trainer ID", example = "2L")
    Long id;

    @Schema(description = "Trainer username", example = "Jane.Smith")
    String username;

    @Schema(description = "Trainer first name", example = "Jane")
    String firstName;

    @Schema(description = "Trainer last name", example = "Smith")
    String lastName;

    @Schema(description = "Training specialization", example = "YOGA")
    String specialization;
}