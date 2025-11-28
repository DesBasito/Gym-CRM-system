package epam.gym.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Trainee request for create and update")
public class TraineeRequest {
    @NotBlank(message = "First name is required")
    @Schema(description = "First name", requiredMode = Schema.RequiredMode.REQUIRED, example = "John")
    String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Last name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Doe")
    String lastName;

    @Schema(description = "Date of birth", example = "1990-01-15")
    LocalDate dateOfBirth;

    @Schema(description = "Address", example = "123 Main St, City")
    String address;
}
