package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.mappers.TrainerMapper;
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
public class TrainerServiceImpl extends AbstractUserService<Trainer, TrainerModel, TrainerRepository, TrainerRequest>
        implements TrainerService {

    private final TrainingTypeRepository trainingTypeRepository;
    private final TraineeRepository traineeRepository;

    @Autowired
    public TrainerServiceImpl(TrainerRepository repo, TrainerMapper mapper, TrainingTypeRepository typeRepo, TraineeRepository traineeRepository) {
        super(repo, mapper);
        this.trainingTypeRepository = typeRepo;
        this.traineeRepository = traineeRepository;
    }

    @Override
    public TrainerModel create(TrainerRequest request) {
        TrainingTypeValidator.parse(request.getSpecialization());
        log.info("Creating trainer: {} {} with specialization: {}",
                request.getFirstName(), request.getLastName(), request.getSpecialization());

        TrainerModel model = mapper.requestToModel(request);
        setGeneratedCredentials(model);

        Trainer entity = mapper.toEntity(model);
        entity.setSpecialization(trainingTypeRepository.findByName(request.getSpecialization()));

        Trainer created = repository.save(entity);
        TrainerModel result = mapper.toModel(created);

        log.info("Trainer created successfully with username: {}", result.getUsername());
        return result;
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
        entity.getUser().setIsActive(request.getIsActive());
        entity.setSpecialization(trainingTypeRepository.findByName(request.getSpecialization()));
    }

    @Override
    protected String getFullName(TrainerRequest request) {
        return String.format("%s %s",request.getFirstName(), request.getLastName());
    }

    @Override
    public List<TrainerModel> findAllNotAssignedToTrainee(String traineeUsername) {
        log.info("Finding trainers not assigned to trainee: {}", traineeUsername);

        Trainee trainee = traineeRepository.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new NoSuchElementException("Trainee not found with username: " + traineeUsername);
        }

        List<Trainer> trainers = repository.findAllNotAssignedToTrainee(traineeUsername);
        List<TrainerModel> trainerModels = trainers.stream()
                .map(mapper::toModel)
                .toList();

        log.info("Found {} trainers not assigned to trainee: {}", trainerModels.size(), traineeUsername);
        return trainerModels;
    }
}
