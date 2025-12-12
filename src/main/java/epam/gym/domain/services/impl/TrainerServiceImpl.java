package epam.gym.domain.services.impl;

import epam.gym.constants.TrainingType;
import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.UpdateTrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.entities.RoleName;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.mappers.TrainerMapper;
import epam.gym.infrastructure.monitoring.metrics.UserMetrics;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import epam.gym.security.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class TrainerServiceImpl extends AbstractUserService<Trainer, TrainerModel, TrainerRepository, TrainerRequest, TrainerProfileDto>
        implements TrainerService {
    private final TrainerMapper trainerMapper;

    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;
    private final RoleService roleService;

    @Autowired
    public TrainerServiceImpl(TrainerRepository repo, TrainerMapper mapper, TrainingTypeRepository typeRepo, TraineeRepository traineeRepository,
                              TrainerMapper trainerMapper, UserMetrics userMetrics, RoleService roleService, PasswordEncoder encoder) {
        super(repo, mapper, userMetrics, encoder);
        this.trainingTypeRepository = typeRepo;
        this.traineeRepository = traineeRepository;
        this.trainerMapper = trainerMapper;
        this.roleService = roleService;
    }

    @Override
    protected Optional<Trainer> findByUsername(String username) {
        return repository.findByUser_Username(username);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return repository.authenticate(username, password);
    }

    @Override
    protected void beforeCreate(Trainer entity, TrainerRequest request) {
        TrainingType type = TrainingType.valueOf(request.getSpecialization().toUpperCase());
        entity.setSpecialization(trainingTypeRepository.findTrainingTypeByTrainingTypeName(type).orElseThrow());
        roleService.assignRoleToUser(entity.getUser(), RoleName.ROLE_TRAINER);
    }

    @Override
    protected void updateEntityFields(Trainer entity, TrainerRequest request) {
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
        TrainingType type = TrainingType.valueOf(request.getSpecialization().toUpperCase());
        entity.setSpecialization(trainingTypeRepository.findTrainingTypeByTrainingTypeName(type).orElseThrow());
    }

    @Override
    protected String getFullName(TrainerRequest request) {
        return String.format("%s %s", request.getFirstName(), request.getLastName());
    }

    @Override
    protected void updateEntityFieldsFromUpdateRequest(Trainer entity, Object updateRequest) {
        UpdateTrainerRequest request = (UpdateTrainerRequest) updateRequest;
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
    }

    @Override
    protected void setIsActiveFromUpdateRequest(Trainer entity, Object updateRequest) {
        UpdateTrainerRequest request = (UpdateTrainerRequest) updateRequest;
        entity.getUser().setIsActive(request.getIsActive());
    }

    @Override
    public TrainerProfileDto updateByUsername(UpdateTrainerRequest request) {
        return super.updateByUsername(request, request.getUsername());
    }

    @Override
    public List<TrainerInfoDto> findAllNotAssignedToTrainee(String traineeUsername) {
        log.info("Finding trainers not assigned to trainee: {}", traineeUsername);

        traineeRepository.findByUser_Username(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Trainee not found with username: " + traineeUsername));

        List<Trainer> trainers = repository.findAllNotAssignedToTrainee(traineeUsername);
        List<TrainerInfoDto> trainerInfoList = trainers.stream()
                .map(mapper::toModel)
                .map(trainerMapper::modelToInfoDto)
                .toList();

        log.info("Found {} trainers not assigned to trainee: {}", trainerInfoList.size(), traineeUsername);
        return trainerInfoList;
    }
}
