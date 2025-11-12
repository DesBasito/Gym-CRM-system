package epam.gym.domain.models;

import lombok.*;
        import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserModel {
    Long id;
    String firstName;
    String lastName;
    String username;
    String password;
    Boolean isActive;
}
