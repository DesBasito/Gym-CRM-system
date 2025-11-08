package epam.gym.domain.dto.request;

import epam.gym.domain.entities.EmbeddedTrainingId;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingCreateRequest {
    EmbeddedTrainingId trainingId;
    String trainingType;
    LocalDate trainingDate;
    String trainingDuration;
}
