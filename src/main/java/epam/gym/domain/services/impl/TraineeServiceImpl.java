package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TraineeServiceImpl extends AbstractUserService<Trainee, TraineeModel, TraineeRepository, TraineeRequest>
        implements TraineeService {

    @Autowired
    public TraineeServiceImpl(TraineeRepository repo, TraineeMapper mapper) {
        super(repo, mapper);
    }

    @Override
    protected void updateEntityFields(Trainee entity, TraineeRequest request) {
        entity.setFirstName(request.getFirstName());
        entity.setLastName(request.getLastName());
        entity.setIsActive(request.getIsActive());
        entity.setAddress(request.getAddress());
        entity.setDateOfBirth(request.getDateOfBirth());
    }

    @Override
    protected String getFullName(TraineeRequest request) {
        return String.format("%s %s",request.getFirstName(), request.getLastName());
    }

    @Override
    protected boolean hasNameChanged(Trainee entity, TraineeRequest request) {
        return !entity.getFirstName().equals(request.getFirstName()) ||
               !entity.getLastName().equals(request.getLastName());
    }

    @Override
    protected Trainee createNewUserEntity(TraineeRequest request) {
        Trainee newEntity = new Trainee();
        newEntity.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                request.getFirstName(), request.getLastName()));
        newEntity.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
        return newEntity;
    }

    @Override
    public void delete(String id) {

    }
}

