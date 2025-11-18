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
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.security.UserContext;
import epam.gym.infrastructure.security.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component("gymFacadeImpl")
@RequiredArgsConstructor
public class GymFacadeImpl implements GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserContext userContext;

    public TraineeModel createTrainee(TraineeRequest traineeRequest) {
        log.info("Facade: Creating trainee {} {}", traineeRequest.getFirstName(), traineeRequest.getLastName());
        return traineeService.create(traineeRequest);
    }

    public TraineeModel updateTrainee(TraineeRequest traineeRequest, Long id) {
        log.info("Facade: Updating trainee with id {}", id);
        return traineeService.update(traineeRequest, id);
    }

    public TraineeModel getTrainee(Long id) {
        log.info("Facade: Getting trainee with id {}", id);
        return traineeService.select(id);
    }

    public void deleteTrainee(Long id) {
        log.info("Facade: Deleting trainee with id {}", id);
        traineeService.delete(id);
    }

    public List<TraineeModel> getAllTrainees(int offset, int limit) {
        log.info("Facade: Getting trainees with offset {} and limit {}", offset, limit);
        return traineeRepository.findAll(offset, limit).stream()
                .map(trainee -> traineeService.select(trainee.getId()))
                .toList();
    }

    public long countTrainees() {
        log.info("Facade: Counting all trainees");
        return traineeRepository.count();
    }

    public boolean authenticateTrainee(String username, String password) {
        log.info("Facade: Authenticating trainee {}", username);
        boolean authenticated = traineeRepository.authenticate(username, password);
        if (authenticated) {
            userContext.login(username, UserRole.TRAINEE);
        }
        return authenticated;
    }

    public void changeTraineePassword(Long id, String newPassword) {
        log.info("Facade: Changing password for trainee {}", id);
        traineeService.changePassword(id, newPassword);
    }

    public void activateTrainee(Long id) {
        log.info("Facade: Activating trainee {}", id);
        traineeService.activate(id);
    }

    public void deactivateTrainee(Long id) {
        log.info("Facade: Deactivating trainee {}", id);
        traineeService.deactivate(id);
    }

    public List<TrainingModel> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType) {
        log.info("Facade: Getting trainings for trainee {} from {} to {}", traineeUsername, fromDate, toDate);
        return trainingService.selectTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);
    }

    public TrainerModel createTrainer(TrainerRequest trainerRequest) {
        log.info("Facade: Creating trainer {} {}", trainerRequest.getFirstName(), trainerRequest.getLastName());
        return trainerService.create(trainerRequest);
    }

    public TrainerModel updateTrainer(TrainerRequest trainerRequest, Long id) {
        log.info("Facade: Updating trainer with id {}", id);
        return trainerService.update(trainerRequest, id);
    }

    public TrainerModel getTrainer(Long id) {
        log.info("Facade: Getting trainer with id {}", id);
        return trainerService.select(id);
    }

    public List<TrainerModel> getAllTrainers(int offset, int limit) {
        log.info("Facade: Getting trainers with offset {} and limit {}", offset, limit);
        return trainerRepository.findAll(offset, limit).stream()
                .map(trainer -> trainerService.select(trainer.getId()))
                .toList();
    }

    public long countTrainers() {
        log.info("Facade: Counting all trainers");
        return trainerRepository.count();
    }

    public List<TrainerModel> getTrainersNotAssignedToTrainee(String traineeUsername) {
        log.info("Facade: Getting trainers not assigned to trainee {}", traineeUsername);
        return trainerService.findAllNotAssignedToTrainee(traineeUsername);
    }

    public boolean authenticateTrainer(String username, String password) {
        log.info("Facade: Authenticating trainer {}", username);
        boolean authenticated = trainerRepository.authenticate(username, password);
        if (authenticated) {
            userContext.login(username, UserRole.TRAINER);
        }
        return authenticated;
    }

    public void changeTrainerPassword(Long id, String newPassword) {
        log.info("Facade: Changing password for trainer {}", id);
        trainerService.changePassword(id, newPassword);
    }

    public void activateTrainer(Long id) {
        log.info("Facade: Activating trainer {}", id);
        trainerService.activate(id);
    }

    public void deactivateTrainer(Long id) {
        log.info("Facade: Deactivating trainer {}", id);
        trainerService.deactivate(id);
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
