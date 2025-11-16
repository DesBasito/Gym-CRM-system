package epam.gym.application;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.models.TrainingModel;

import java.time.LocalDate;
import java.util.List;

public interface GymFacadeInterface {

    // TRAINEE
    TraineeModel createTrainee(TraineeRequest traineeRequest);
    TraineeModel updateTrainee(TraineeRequest traineeRequest, Long id);
    TraineeModel getTrainee(Long id);
    void deleteTrainee(Long id);
    List<TraineeModel> getAllTrainees();
    boolean authenticateTrainee(String username, String password);
    void changeTraineePassword(Long id, String newPassword);
    void activateTrainee(Long id);
    void deactivateTrainee(Long id);
    List<TrainingModel> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType);

    // TRAINER
    TrainerModel createTrainer(TrainerRequest trainerRequest);
    TrainerModel updateTrainer(TrainerRequest trainerRequest, Long id);
    TrainerModel getTrainer(Long id);
    List<TrainerModel> getAllTrainers();
    List<TrainerModel> getTrainersNotAssignedToTrainee(String traineeUsername);
    boolean authenticateTrainer(String username, String password);
    void changeTrainerPassword(Long id, String newPassword);
    void activateTrainer(Long id);
    void deactivateTrainer(Long id);
    List<TrainingModel> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate);

    // TRAINING
    TrainingModel createTraining(TrainingRequest trainingRequest);
}
