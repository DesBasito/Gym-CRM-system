package epam.gym.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Change password request")
public class ChangePasswordRequest {
    @NotBlank(message = "Old password is required")
    @Schema(description = "Old password", requiredMode = Schema.RequiredMode.REQUIRED, example = "oldPassword123")
    String oldPassword;

    @NotBlank(message = "New password is required")
    @Schema(description = "New password", requiredMode = Schema.RequiredMode.REQUIRED, example = "newPassword456")
    String newPassword;
}