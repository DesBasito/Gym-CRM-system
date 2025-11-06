package epam.gym.infrastructure.dao;

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
public class TraineeDao extends UserDao {
    LocalDate dateOfBirth;
    String address;
}
