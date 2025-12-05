package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.domain.models.TrainingModel;

import java.util.List;

public interface TrainingService {
    TrainingModel create(TrainingRequest trainingRequest);
    List<TrainingDto> selectTraineeTrainings(TraineeTrainingsFilterRequest filterRequest);
    List<TrainingDto> selectTrainerTrainings(TrainerTrainingsFilterRequest filterRequest);
    List<TrainingTypeDto> getAllTrainingTypes();
}
