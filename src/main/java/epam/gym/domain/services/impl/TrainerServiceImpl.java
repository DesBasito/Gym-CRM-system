package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.request.UpdateTrainerRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.mappers.TrainerMapper;
import epam.gym.infrastructure.monitoring.metrics.UserMetrics;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingTypeRepository;
import epam.gym.util.TrainingTypeValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class TrainerServiceImpl extends AbstractUserService<Trainer, TrainerModel, TrainerRepository, TrainerRequest, TrainerProfileDto>
        implements TrainerService {
    private final TrainerMapper trainerMapper;

    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;

    @Autowired
    public TrainerServiceImpl(TrainerRepository repo, TrainerMapper mapper, TrainingTypeRepository typeRepo, TraineeRepository traineeRepository,
                              TrainerMapper trainerMapper, UserMetrics userMetrics) {
        super(repo, mapper, userMetrics);
        this.trainingTypeRepository = typeRepo;
        this.traineeRepository = traineeRepository;
        this.trainerMapper = trainerMapper;
    }

    @Override
    protected void beforeCreate(Trainer entity, TrainerRequest request) {
        entity.setSpecialization(trainingTypeRepository.findByName(request.getSpecialization()));
    }

    @Override
    public RegistrationResponse create(TrainerRequest request) {
        TrainingTypeValidator.parse(request.getSpecialization());
        return super.create(request);
    }

    @Override
    public TrainerModel update(TrainerRequest request, Long id) {
        TrainingTypeValidator.parse(request.getSpecialization());
        return super.update(request, id);
    }

    @Override
    protected void updateEntityFields(Trainer entity, TrainerRequest request) {
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
        entity.setSpecialization(trainingTypeRepository.findByName(request.getSpecialization()));
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

        Trainee trainee = traineeRepository.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new NoSuchElementException("Trainee not found with username: " + traineeUsername);
        }

        List<Trainer> trainers = repository.findAllNotAssignedToTrainee(traineeUsername);
        List<TrainerInfoDto> trainerInfoList = trainers.stream()
                .map(mapper::toModel)
                .map(trainerMapper::modelToInfoDto)
                .toList();

        log.info("Found {} trainers not assigned to trainee: {}", trainerInfoList.size(), traineeUsername);
        return trainerInfoList;
    }
}
