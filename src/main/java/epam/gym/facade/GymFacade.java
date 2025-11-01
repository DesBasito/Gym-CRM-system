package epam.gym.facade;

import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.dto.TrainerDto;
import epam.gym.domain.dto.TrainingDto;
import epam.gym.domain.entities.EmbeddedTrainingId;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.Trainer;
import epam.gym.domain.entities.Training;
import epam.gym.services.interfaces.TraineeService;
import epam.gym.services.interfaces.TrainerService;
import epam.gym.services.interfaces.TrainingService;
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

    public Trainee createTrainee(TraineeDto traineeDto) {
        log.info("Facade: Creating trainee {} {}", traineeDto.getFirstName(), traineeDto.getLastName());
        return traineeService.create(traineeDto);
    }

    public Trainee updateTrainee(TraineeDto traineeDto, String username) {
        log.info("Facade: Updating trainee with username {}", username);
        return traineeService.update(traineeDto, username);
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

    public Trainer createTrainer(TrainerDto trainerDto) {
        log.info("Facade: Creating trainer {} {}", trainerDto.getFirstName(), trainerDto.getLastName());
        return trainerService.create(trainerDto);
    }

    public Trainer updateTrainer(TrainerDto trainerDto, String username) {
        log.info("Facade: Updating trainer with username {}", username);
        return trainerService.update(trainerDto, username);
    }

    public Trainer getTrainer(String userId) {
        log.info("Facade: Getting trainer with userId {}", userId);
        return trainerService.select(userId);
    }

    // ============= Training =============

    public Training createTraining(TrainingDto trainingDto) {
        log.info("Facade: Creating training {}", trainingDto.getTrainingId());
        return trainingService.create(trainingDto);
    }

    public Training getTraining(EmbeddedTrainingId trainingId) {
        log.info("Facade: Getting training with id {}", trainingId);
        return trainingService.select(trainingId);
    }
}