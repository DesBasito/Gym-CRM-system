package epam.gym.domain.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Trainee profile details")
public class TraineeProfileDto {
    @Schema(description = "Trainee ID", example = "1L")
    Long id;

    @Schema(description = "Username", example = "John.Doe")
    String username;

    @Schema(description = "First name", example = "John")
    String firstName;

    @Schema(description = "Last name", example = "Doe")
    String lastName;

    @Schema(description = "Date of birth", example = "1990-01-15")
    LocalDate dateOfBirth;

    @Schema(description = "Address", example = "123 Main St, City")
    String address;

    @Schema(description = "Is active", example = "true")
    Boolean isActive;

    @Schema(description = "List of assigned trainers")
    List<TrainerInfoDto> trainers;
}