package epam.gym.domain.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    String firstName;
    String lastName;
    String username;
    String password;
    Boolean isActive;
}
