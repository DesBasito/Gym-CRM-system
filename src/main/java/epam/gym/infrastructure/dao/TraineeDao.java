package epam.gym.infrastructure.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TraineeDao {
    String username;
    LocalDate dateOfBirth;
    String address;
}
