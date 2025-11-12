package epam.gym.domain.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TrainingModel {
    Long id;
    Long trainerId;
    Long traineeId;
    String trainingName;
    Long trainingTypeId;
    LocalDate trainingDate;
    String trainingDuration;
}
