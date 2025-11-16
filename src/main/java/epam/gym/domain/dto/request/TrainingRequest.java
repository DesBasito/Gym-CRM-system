package epam.gym.domain.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingRequest {
    String trainerUsername;
    String traineeUsername;
    String trainingName;
    String trainingType;
    LocalDate trainingDate;
    Integer trainingDuration;
}
