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
@Schema(description = "Trainee basic information")
public class TraineeInfoDto {
    @Schema(description = "Trainee ID", example = "1L")
    Long id;

    @Schema(description = "Trainee username", example = "John.Doe")
    String username;

    @Schema(description = "Trainee first name", example = "John")
    String firstName;

    @Schema(description = "Trainee last name", example = "Doe")
    String lastName;
}