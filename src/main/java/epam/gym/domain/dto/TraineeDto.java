package epam.gym.domain.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeDto {
    String firstName;
    String lastName;
    LocalDate dateOfBirth;
    String address;
    Boolean isActive;
}
