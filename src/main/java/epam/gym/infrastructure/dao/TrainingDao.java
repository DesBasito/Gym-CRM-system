package epam.gym.infrastructure.dao;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingDao {
    EmbeddedTrainingDaoId trainingDaoId;
    String trainingType;
    LocalDate trainingDate;
    String trainingDuration;
}
