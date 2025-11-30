package epam.gym.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingDto {
    private String trainingName;
    private LocalDate trainingDate;
    private String trainingType;
    private Integer trainingDuration;
    private String trainerName;
}