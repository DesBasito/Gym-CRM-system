package epam.gym.services.impl;

import epam.gym.dao.impl.TrainingDao;
import epam.gym.domain.dto.TrainingDto;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import epam.gym.services.interfaces.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    @Autowired
    private TrainingDao trainingDao;

    @Override
    public Training create(TrainingDto trainingDto) {
        log.info("Creating training: {}", trainingDto.getTrainingId());

        Training training = new Training();
        training.setTrainingId(trainingDto.getTrainingId());
        training.setTrainingType(trainingDto.getTrainingType());
        training.setTrainingDate(trainingDto.getTrainingDate());
        training.setTrainingDuration(trainingDto.getTrainingDuration());

        Training createdTraining = trainingDao.create(training).orElse(null);

        if (createdTraining == null) log.error("Failed to create training: {}", trainingDto.getTrainingId());
        else log.info("Training created successfully: {}", createdTraining.getTrainingId());


        return createdTraining;
    }

    @Override
    public Training select(EmbeddedTrainingId id) {
        log.info("Selecting training by id: {}", id);
        Training training = trainingDao.select(id).orElse(null);

        if (training == null) log.error("Training not found with id: {}", id);
        else log.info("Training found with id: {}", id);

        return training;
    }
}