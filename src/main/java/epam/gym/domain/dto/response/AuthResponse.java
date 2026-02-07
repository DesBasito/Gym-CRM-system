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
@Schema(description = "Authentication response with JWT token")
public class AuthResponse {
    @Schema(description = "JWT access token")
    String token;

    @Schema(description = "Token type")
    String type = "Bearer";

    @Schema(description = "Username of authenticated user", example = "Bla.bla")
    String username;
}