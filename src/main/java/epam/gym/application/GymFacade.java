package epam.gym.application;

import epam.gym.domain.dto.request.TraineeCreationRequest;
import epam.gym.domain.dto.request.TrainerCreationRequest;
import epam.gym.domain.dto.request.TrainingCreateRequest;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.Trainer;
import epam.gym.domain.entities.Training;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    // ============= Trainee =============

    public Trainee createTrainee(TraineeCreationRequest traineeCreationRequest) {
        log.info("Facade: Creating trainee {} {}", traineeCreationRequest.getFirstName(), traineeCreationRequest.getLastName());
        return traineeService.create(traineeCreationRequest);
    }

    public Trainee updateTrainee(TraineeCreationRequest traineeCreationRequest, String username) {
        log.info("Facade: Updating trainee with username {}", username);
        return traineeService.update(traineeCreationRequest, username);
    }

    public Trainee getTrainee(String userId) {
        log.info("Facade: Getting trainee with userId {}", userId);
        return traineeService.select(userId);
    }

    public void deleteTrainee(String userId) {
        log.info("Facade: Deleting trainee with userId {}", userId);
        traineeService.delete(userId);
    }

    // ============= Trainer =============

    public Trainer createTrainer(TrainerCreationRequest trainerDto) {
        log.info("Facade: Creating trainer {} {}", trainerDto.getFirstName(), trainerDto.getLastName());
        return trainerService.create(trainerDto);
    }

    public Trainer updateTrainer(TrainerCreationRequest trainerDto, String username) {
        log.info("Facade: Updating trainer with username {}", username);
        return trainerService.update(trainerDto, username);
    }

    public Trainer getTrainer(String userId) {
        log.info("Facade: Getting trainer with userId {}", userId);
        return trainerService.select(userId);
    }

    // ============= Training =============

    public Training createTraining(TrainingCreateRequest trainingCreateRequest) {
        log.info("Facade: Creating training {}", trainingCreateRequest.getTrainingId());
        return trainingService.create(trainingCreateRequest);
    }

    public Training getTraining(EmbeddedTrainingId trainingId) {
        log.info("Facade: Getting training with id {}", trainingId);
        return trainingService.select(trainingId);
    }
}