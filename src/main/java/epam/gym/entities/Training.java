package epam.gym.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Training {
    String trainerId;
    String traineeId;
    String trainingName;
    String trainingType;
    LocalDate trainingDate;
    String trainingDuration;
}
