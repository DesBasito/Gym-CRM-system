package epam.gym.domain.models;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeModel extends UserModel {
    Long id;
    Long userId;
    LocalDate dateOfBirth;
    String address;
}
