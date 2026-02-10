package epam.gym.domain.services.impl;

import epam.gym.constants.TrainingType;
import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.domain.services.interfaces.WorkloadService;
import epam.gym.infrastructure.client.dto.WorkloadRequest;
import epam.gym.infrastructure.entities.Training;
import epam.gym.infrastructure.mappers.TrainingMapper;
import epam.gym.infrastructure.mappers.TrainingTypeMapper;
import epam.gym.infrastructure.monitoring.metrics.TrainingMetrics;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import epam.gym.infrastructure.specifications.TrainingSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingMapper mapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final TrainingMetrics trainingMetrics;
    private final WorkloadService workloadService;

    @Transactional
    @Override
    public TrainingModel create(TrainingRequest trainingRequest) {
        log.info("Creating training: {}", trainingRequest.getTrainingName());

        Training training = getTraining(trainingRequest);

        if (!training.getTrainer().getTrainees().contains(training.getTrainee())) {
            training.getTrainer().getTrainees().add(training.getTrainee());
            training.getTrainee().getTrainers().add(training.getTrainer());
        }

        training.getTrainee().addTraining(training);
        training.getTrainer().addTraining(training);

        Training createdTraining = trainingRepository.save(training);
        TrainingModel trainingModel = mapper.entityToModel(createdTraining);

        trainingMetrics.incrementTrainingCreated();
        trainingMetrics.incrementActiveTrainings();

        // Notify workload-service about new training (async, non-blocking)
        notifyWorkloadService(createdTraining, WorkloadRequest.ActionType.ADD);

        log.info("Training created successfully: {}", trainingModel.getTrainingName());
        return trainingModel;
    }

    private Training getTraining(TrainingRequest trainingRequest) {
        Training training = mapper.requestToEntity(trainingRequest);
        TrainingType type = TrainingType.valueOf(trainingRequest.getTrainingType().toUpperCase());
        training.setTrainingType(trainingTypeRepository.findTrainingTypeByTrainingTypeName(type)
                .orElseThrow()
        );
        training.setTrainee(traineeRepository.findByUser_Username(trainingRequest.getTraineeUsername())
                .orElseThrow(() -> new NoSuchElementException("Trainee not found with username: " + trainingRequest.getTraineeUsername())));
        training.setTrainer(trainerRepository.findByUser_Username(trainingRequest.getTrainerUsername())
                .orElseThrow(() -> new NoSuchElementException("Trainer not found with username: " + trainingRequest.getTrainerUsername())));
        return training;
    }

    @Transactional
    @Override
    public List<TrainingDto> selectTraineeTrainings(TraineeTrainingsFilterRequest filterRequest) {
        log.info("Selecting trainee trainings for username: {} with filters: from={}, to={}, trainer={}, type={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTrainerName(), filterRequest.getTrainingType());

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTraineeTrainings(filterRequest));
        return trainings.stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Transactional
    @Override
    public List<TrainingDto> selectTrainerTrainings(TrainerTrainingsFilterRequest filterRequest) {
        log.info("Selecting trainer trainings for username: {} with filters: from={}, to={}, trainee={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTraineeName());

        List<Training> trainings = trainingRepository.findAll(TrainingSpecification.filterTrainerTrainings(filterRequest));
        return trainings.stream()
                .map(mapper::entityToDto)
                .toList();
    }

    @Override
    public List<TrainingTypeDto> getAllTrainingTypes() {
        log.info("Getting all Training types");
        return trainingTypeRepository.findAll().stream().map(trainingTypeMapper::toDto).toList();
    }

    protected void notifyWorkloadService(Training training, WorkloadRequest.ActionType actionType) {
        try {
            WorkloadRequest request = WorkloadRequest.builder()
                    .username(training.getTrainer().getUser().getUsername())
                    .firstName(training.getTrainer().getUser().getFirstName())
                    .lastName(training.getTrainer().getUser().getLastName())
                    .isActive(training.getTrainer().getUser().getIsActive())
                    .trainingDate(training.getTrainingDate())
                    .trainingDuration(training.getTrainingDuration())
                    .actionType(actionType)
                    .build();

            log.info("Notifying workload-service about training {} for trainer: {}",
                    actionType, training.getTrainer().getUser().getUsername());

            workloadService.updateWorkload(request);

            log.debug("Successfully notified workload-service for trainer: {}",
                    training.getTrainer().getUser().getUsername());
        } catch (Exception e) {
            log.error("Failed to notify workload-service for trainer: {}. Error: {}",
                    training.getTrainer().getUser().getUsername(), e.getMessage(), e);
        }
    }
}