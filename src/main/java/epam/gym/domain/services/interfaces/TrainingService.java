package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.domain.models.TrainingModel;
import epam.gym.infrastructure.entities.TrainingType;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    TrainingModel create(TrainingRequest trainingRequest);
    List<TrainingDto> selectTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType);
    List<TrainingDto> selectTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate);
    List<TrainingTypeDto> getAllTrainingTypes();
}
