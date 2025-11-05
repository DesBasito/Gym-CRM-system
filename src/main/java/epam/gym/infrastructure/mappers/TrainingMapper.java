package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Training;
import epam.gym.infrastructure.dao.EmbeddedTrainingDaoId;
import epam.gym.infrastructure.dao.TrainingDao;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {
    public TrainingDao toDao(Training training) {
        if (training == null) {
            throw new IllegalArgumentException("Training cannot be empty!");
        }

        TrainingDao trainingDao = new TrainingDao();
        trainingDao.setTrainingDaoId(toDao(training.getTrainingId()));
        trainingDao.setTrainingType(training.getTrainingType());
        trainingDao.setTrainingDate(training.getTrainingDate());
        trainingDao.setTrainingDuration(training.getTrainingDuration());

        return trainingDao;
    }

    public Training toModel(TrainingDao trainingDao) {
        if (trainingDao == null) {
            throw new IllegalArgumentException("Training cannot be empty!");
        }

        EmbeddedTrainingId trainingId = toModel(trainingDao.getTrainingDaoId());

        return Training.builder()
                .trainingId(trainingId)
                .trainingType(trainingDao.getTrainingType())
                .trainingDate(trainingDao.getTrainingDate())
                .trainingDuration(trainingDao.getTrainingDuration())
                .build();
    }

    private EmbeddedTrainingDaoId toDao(EmbeddedTrainingId id) {
        if (id == null) {
            return null;
        }

        EmbeddedTrainingDaoId daoId = new EmbeddedTrainingDaoId();
        daoId.setTraineeId(id.getTraineeId());
        daoId.setTrainerId(id.getTrainerId());
        daoId.setTrainingName(id.getTrainingName());

        return daoId;
    }

    private EmbeddedTrainingId toModel(EmbeddedTrainingDaoId id) {
        if (id == null) {
            return null;
        }

        return EmbeddedTrainingId.builder()
                .traineeId(id.getTraineeId())
                .trainerId(id.getTrainerId())
                .trainingName(id.getTrainingName())
                .build();
    }
}