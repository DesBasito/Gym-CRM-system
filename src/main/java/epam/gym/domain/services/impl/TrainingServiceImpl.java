package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainingCreateRequest;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.repositories.impl.TrainingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;

    @Override
    public Training create(TrainingCreateRequest trainingCreateRequest) {
        log.info("Creating training: {}", trainingCreateRequest.getTrainingId());

        Training training = new Training();
        training.setTrainingId(trainingCreateRequest.getTrainingId());
        training.setTrainingType(trainingCreateRequest.getTrainingType());
        training.setTrainingDate(trainingCreateRequest.getTrainingDate());
        training.setTrainingDuration(trainingCreateRequest.getTrainingDuration());

        Training createdTraining = trainingRepository.save(training);

        if (createdTraining == null) log.error("Failed to create training: {}", trainingCreateRequest.getTrainingId());
        else log.info("Training created successfully: {}", createdTraining.getTrainingId());


        return createdTraining;
    }

    @Override
    public Training select(EmbeddedTrainingId id) {
        log.info("Selecting training by id: {}", id);
        Training training = trainingRepository.select(id);

        if (training == null) log.error("Training not found with id: {}", id);
        else log.info("Training found with id: {}", id);

        return training;
    }
}