package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TrainingModel;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingModel create(TrainingRequest trainingRequest);
    List<TrainingModel> selectTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType);
    List<TrainingModel> selectTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate);
}
