package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.mappers.TrainingMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import epam.gym.util.TrainingTypeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingMapper mapper;

    @Transactional
    @Override
    public TrainingModel create(TrainingRequest trainingRequest) {
        log.info("Creating training: {}", trainingRequest.getTrainingName());

        Training training = getTraining(trainingRequest);
        Training createdTraining = trainingRepository.save(training);
        TrainingModel trainingModel = mapper.entityToModel(createdTraining);

        log.info("Training created successfully: {}", trainingModel.getTrainingName());
        return trainingModel;
    }

    private Training getTraining(TrainingRequest trainingRequest) {
        TrainingTypeValidator.parse(trainingRequest.getTrainingType());
        Training training = mapper.requestToEntity(trainingRequest);
        training.setTrainingType(trainingTypeRepository.findByName(trainingRequest.getTrainingType()));
        training.setTrainee(traineeRepository.findByUsername(trainingRequest.getTraineeUsername()));
        training.setTrainer(trainerRepository.findByUsername(trainingRequest.getTrainerUsername()));
        return training;
    }

    @Transactional
    @Override
    public List<TrainingModel> selectTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType) {
        log.info("Selecting trainee trainings for username: {}", traineeUsername);

        List<Training> trainings = trainingRepository.findTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);
        return trainings.stream()
                .map(mapper::entityToModel)
                .toList();
    }

    @Transactional
    @Override
    public List<TrainingModel> selectTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate) {
        log.info("Selecting trainer trainings for username: {}", trainerUsername);

        List<Training> trainings = trainingRepository.findTrainerTrainings(trainerUsername, fromDate, toDate);
        return trainings.stream()
                .map(mapper::entityToModel)
                .toList();
    }
}