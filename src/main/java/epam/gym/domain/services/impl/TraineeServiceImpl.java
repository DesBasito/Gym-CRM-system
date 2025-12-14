package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.request.UpdateTraineeRequest;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.constants.RoleName;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.mappers.TrainerMapper;
import epam.gym.infrastructure.monitoring.metrics.UserMetrics;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.security.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class TraineeServiceImpl extends AbstractUserService<Trainee, TraineeModel, TraineeRepository, TraineeRequest, TraineeProfileDto>
        implements TraineeService {

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final RoleService roleService;

    @Autowired
    public TraineeServiceImpl(TraineeRepository repo, TraineeMapper mapper, TrainerRepository trainerRepository, TrainerMapper trainerMapper, UserMetrics userMetrics, RoleService roleService, PasswordEncoder passwordEncoder) {
        super(repo, mapper, userMetrics, passwordEncoder);
        this.trainerRepository = trainerRepository;
        this.trainerMapper = trainerMapper;
        this.roleService = roleService;
    }

    @Override
    protected void beforeCreate(Trainee entity, TraineeRequest request) {
        roleService.assignRoleToUser(entity.getUser(), RoleName.ROLE_TRAINEE);
    }

    @Override
    protected Optional<Trainee> findByUsername(String username) {
        return repository.findByUser_Username(username);
    }

    @Override
    protected boolean authenticate(String username, String password) {
        return repository.authenticate(username, password);
    }

    @Override
    protected void updateEntityFields(Trainee entity, TraineeRequest request) {
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
        entity.setAddress(request.getAddress());
        entity.setDateOfBirth(request.getDateOfBirth());
    }

    @Override
    protected String getFullName(TraineeRequest request) {
        return String.format("%s %s", request.getFirstName(), request.getLastName());
    }

    @Override
    protected void updateEntityFieldsFromUpdateRequest(Trainee entity, Object updateRequest) {
        UpdateTraineeRequest request = (UpdateTraineeRequest) updateRequest;
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
        entity.setDateOfBirth(request.getDateOfBirth());
        entity.setAddress(request.getAddress());
    }

    @Override
    protected void setIsActiveFromUpdateRequest(Trainee entity, Object updateRequest) {
        UpdateTraineeRequest request = (UpdateTraineeRequest) updateRequest;
        entity.getUser().setIsActive(request.getIsActive());
    }

    @Override
    public TraineeProfileDto updateByUsername(UpdateTraineeRequest request) {
        return super.updateByUsername(request, request.getUsername());
    }

    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public void delete(String username) {
        log.info("Deleting trainee by username: {}", username);

        Trainee trainee = repository.findByUser_Username(username)
                .orElseThrow(
                        ()-> new NoSuchElementException("Trainee not found with username: " + username));

        trainee.getTrainings().clear();

        repository.delete(trainee);
        log.info("Trainee deleted successfully with username: {}", username);
    }

    @Override
    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public List<TrainerInfoDto> updateTrainersList(String traineeUsername, List<String> trainerUsernames) {
        log.info("Updating trainers list for trainee: {}", traineeUsername);

        Trainee trainee = repository.findByUser_Username(traineeUsername)
                .orElseThrow(() -> new NoSuchElementException("Trainee not found with username: " + traineeUsername));

        List<Trainer> newTrainers = trainerUsernames.stream()
                .map(username -> trainerRepository.findByUser_Username(username)
                        .orElseThrow(() -> new NoSuchElementException("Trainer not found with username: " + username)))
                .toList();

        trainee.setTrainers(new HashSet<>(newTrainers));
        repository.save(trainee);

        List<TrainerInfoDto> result = newTrainers.stream()
                .map(trainerMapper::toModel)
                .map(trainerMapper::modelToInfoDto)
                .toList();

        log.info("Updated trainers list for trainee: {}, new trainers count: {}", traineeUsername, result.size());
        return result;
    }
}

