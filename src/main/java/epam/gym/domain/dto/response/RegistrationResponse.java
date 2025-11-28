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
@Schema(description = "Registration response with generated credentials")
public class RegistrationResponse {
    @Schema(description = "Generated username", requiredMode = Schema.RequiredMode.REQUIRED, example = "John.Doe")
    String username;

    @Schema(description = "Generated password", requiredMode = Schema.RequiredMode.REQUIRED, example = "abc123xyz")
    String password;
}
