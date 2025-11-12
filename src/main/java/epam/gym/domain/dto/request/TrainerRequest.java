package epam.gym.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerRequest {
    String firstName;
    String lastName;
    String specialization;
    Boolean isActive;
}
