package epam.gym.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@Schema(description = "TrainingTypeDto")
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class TrainingTypeDto {
    @Schema(description = "Training type Id", example = "999L")
    Long id;

    @Schema(description = "Training type", example = "YOGA")
    String trainingTypeName;
}
