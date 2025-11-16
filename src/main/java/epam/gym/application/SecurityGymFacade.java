package epam.gym.application;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.models.TrainingModel;
import epam.gym.domain.services.interfaces.SecurityService;
import epam.gym.infrastructure.security.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@Primary
public class SecurityGymFacade implements GymFacade {

    private final GymFacade delegate;
    private final SecurityService securityService;

    @Autowired
    public SecurityGymFacade(@Qualifier("gymFacadeImpl") GymFacade delegate,
                             SecurityService securityService) {
        this.delegate = delegate;
        this.securityService = securityService;
    }

    @Override
    public TraineeModel createTrainee(TraineeRequest traineeRequest) {
        return delegate.createTrainee(traineeRequest);
    }

    @Override
    public TraineeModel updateTrainee(TraineeRequest traineeRequest, Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireTraineeOwnershipById(id);
        return delegate.updateTrainee(traineeRequest, id);
    }

    @Override
    public TraineeModel getTrainee(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        TraineeModel model = delegate.getTrainee(id);
        securityService.requireOwnership(model.getUsername());
        return model;
    }

    @Override
    public void deleteTrainee(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireTraineeOwnershipById(id);
        delegate.deleteTrainee(id);
    }

    @Override
    public List<TraineeModel> getAllTrainees() {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        return delegate.getAllTrainees();
    }

    @Override
    public boolean authenticateTrainee(String username, String password) {
        return delegate.authenticateTrainee(username, password);
    }

    @Override
    public void changeTraineePassword(Long id, String newPassword) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireTraineeOwnershipById(id);
        delegate.changeTraineePassword(id, newPassword);
    }

    @Override
    public void activateTrainee(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireTraineeOwnershipById(id);
        delegate.activateTrainee(id);
    }

    @Override
    public void deactivateTrainee(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireTraineeOwnershipById(id);
        delegate.deactivateTrainee(id);
    }

    @Override
    public List<TrainingModel> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainingType) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireOwnership(traineeUsername);
        return delegate.getTraineeTrainings(traineeUsername, fromDate, toDate, trainingType);
    }

    @Override
    public TrainerModel createTrainer(TrainerRequest trainerRequest) {
        return delegate.createTrainer(trainerRequest);
    }

    @Override
    public TrainerModel updateTrainer(TrainerRequest trainerRequest, Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        securityService.requireTrainerOwnershipById(id);
        return delegate.updateTrainer(trainerRequest, id);
    }

    @Override
    public TrainerModel getTrainer(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        TrainerModel model = delegate.getTrainer(id);
        securityService.requireOwnership(model.getUsername());
        return model;
    }

    @Override
    public List<TrainerModel> getAllTrainers() {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        return delegate.getAllTrainers();
    }

    @Override
    public List<TrainerModel> getTrainersNotAssignedToTrainee(String traineeUsername) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINEE);
        securityService.requireOwnership(traineeUsername);
        return delegate.getTrainersNotAssignedToTrainee(traineeUsername);
    }

    @Override
    public boolean authenticateTrainer(String username, String password) {
        return delegate.authenticateTrainer(username, password);
    }

    @Override
    public void changeTrainerPassword(Long id, String newPassword) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        securityService.requireTrainerOwnershipById(id);
        delegate.changeTrainerPassword(id, newPassword);
    }

    @Override
    public void activateTrainer(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        securityService.requireTrainerOwnershipById(id);
        delegate.activateTrainer(id);
    }

    @Override
    public void deactivateTrainer(Long id) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        securityService.requireTrainerOwnershipById(id);
        delegate.deactivateTrainer(id);
    }

    @Override
    public List<TrainingModel> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate) {
        securityService.requireAuth();
        securityService.requireRole(UserRole.TRAINER);
        securityService.requireOwnership(trainerUsername);
        return delegate.getTrainerTrainings(trainerUsername, fromDate, toDate);
    }

    @Override
    public TrainingModel createTraining(TrainingRequest trainingRequest) {
        securityService.requireAuth();
        return delegate.createTraining(trainingRequest);
    }
}
