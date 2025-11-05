package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.TrainingCreateRequest;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;

public interface TrainingService {
    Training create(TrainingCreateRequest trainingCreateRequest);
    Training select(EmbeddedTrainingId id);
}
