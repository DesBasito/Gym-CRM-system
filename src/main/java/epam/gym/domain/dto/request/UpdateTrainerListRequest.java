package epam.gym.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Update trainee's trainer list request")
public class UpdateTrainerListRequest {
    @NotBlank(message = "Trainee username is required")
    @Schema(description = "Trainee username", requiredMode = Schema.RequiredMode.REQUIRED, example = "John.Doe")
    String traineeUsername;

    @NotEmpty(message = "Trainers list is required")
    @Schema(description = "List of trainer usernames", requiredMode = Schema.RequiredMode.REQUIRED, example = "[\"Jane.Smith\", \"Mike.Johnson\"]")
    List<String> trainerUsernames;
}
