package epam.gym.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainerDto {
    String firstName;
    String lastName;
    String specialization;
    Boolean isActive;
}
