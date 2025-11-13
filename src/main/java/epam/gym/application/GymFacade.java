package epam.gym.application;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.domain.services.impl.TrainerServiceImpl;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TrainerServiceImpl trainerServiceImpl;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    public TraineeModel createTrainee(TraineeRequest traineeRequest) {
        log.info("Facade: Creating trainee {} {}", traineeRequest.getFirstName(), traineeRequest.getLastName());
        return traineeService.create(traineeRequest);
    }

    public TraineeModel updateTrainee(TraineeRequest traineeRequest, String username) {
        log.info("Facade: Updating trainee with username {}", username);
        return traineeService.update(traineeRequest, username);
    }

    public TraineeModel getTrainee(String username) {
        log.info("Facade: Getting trainee with username {}", username);
        return traineeService.select(username);
    }

    public void deleteTrainee(String username) {
        log.info("Facade: Deleting trainee with username {}", username);
        traineeService.delete(username);
    }

    public List<TraineeModel> getAllTrainees() {
        log.info("Facade: Getting all trainees");
        return traineeRepository.findAll().stream()
                .map(trainee -> traineeService.select(trainee.getUsername()))
                .toList();
    }

    public boolean authenticateTrainee(String username, String password) {
        log.info("Facade: Authenticating trainee {}", username);
        return traineeRepository.authenticate(username, password);
    }

    public void changeTraineePassword(String username, String newPassword) {
        log.info("Facade: Changing password for trainee {}", username);
        traineeService.changePassword(username, newPassword);
    }

    public void activateTrainee(String username) {
        log.info("Facade: Activating trainee {}", username);
        traineeService.activate(username);
    }

    public void deactivateTrainee(String username) {
        log.info("Facade: Deactivating trainee {}", username);
        traineeService.deactivate(username);
    }

    public List<TrainingModel> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate) {
        log.info("Facade: Getting trainings for trainee {} from {} to {}", traineeUsername, fromDate, toDate);
        return trainingService.selectTraineeTrainings(traineeUsername, fromDate, toDate);
    }

    public TrainerModel createTrainer(TrainerRequest trainerRequest) {
        log.info("Facade: Creating trainer {} {}", trainerRequest.getFirstName(), trainerRequest.getLastName());
        return trainerService.create(trainerRequest);
    }

    public TrainerModel updateTrainer(TrainerRequest trainerRequest, String username) {
        log.info("Facade: Updating trainer with username {}", username);
        return trainerService.update(trainerRequest, username);
    }

    public TrainerModel getTrainer(String username) {
        log.info("Facade: Getting trainer with username {}", username);
        return trainerService.select(username);
    }

    public List<TrainerModel> getAllTrainers() {
        log.info("Facade: Getting all trainers");
        return trainerRepository.findAll().stream()
                .map(trainer -> trainerService.select(trainer.getUsername()))
                .toList();
    }

    public List<TrainerModel> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.info("Facade: Getting trainers not assigned to trainee {}", traineeUsername);
        return trainerServiceImpl.findAllNotAssignedToTrainee(traineeUsername);
    }

    public boolean authenticateTrainer(String username, String password) {
        log.info("Facade: Authenticating trainer {}", username);
        return trainerRepository.authenticate(username, password);
    }

    public void changeTrainerPassword(String username, String newPassword) {
        log.info("Facade: Changing password for trainer {}", username);
        trainerService.changePassword(username, newPassword);
    }

    public void activateTrainer(String username) {
        log.info("Facade: Activating trainer {}", username);
        trainerService.activate(username);
    }

    public void deactivateTrainer(String username) {
        log.info("Facade: Deactivating trainer {}", username);
        trainerService.deactivate(username);
    }

    public List<TrainingModel> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate) {
        log.info("Facade: Getting trainings for trainer {} from {} to {}", trainerUsername, fromDate, toDate);
        return trainingService.selectTrainerTrainings(trainerUsername, fromDate, toDate);
    }

    public TrainingModel createTraining(TrainingRequest trainingRequest) {
        log.info("Facade: Creating training {}", trainingRequest.getTrainingName());
        return trainingService.create(trainingRequest);
    }
}
