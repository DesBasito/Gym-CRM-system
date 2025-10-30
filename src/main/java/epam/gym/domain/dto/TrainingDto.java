package epam.gym.domain.dto;

import epam.gym.domain.entities.EmbeddedTrainingId;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingDto {
    EmbeddedTrainingId trainingId;
    String trainingType;
    LocalDate trainingDate;
    String trainingDuration;
}
