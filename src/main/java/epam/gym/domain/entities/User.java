package epam.gym.domain.entities;

import lombok.*;
        import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class User {
    String firstName;
    String lastName;
    String username;
    String password;
    Boolean isActive;
}
