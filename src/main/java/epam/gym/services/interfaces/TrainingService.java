package epam.gym.services.interfaces;

import epam.gym.domain.dto.TrainingDto;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.Training;

public interface TrainingService {
    Training create(TrainingDto trainingDto);
    Training select(EmbeddedTrainingId id);
}
