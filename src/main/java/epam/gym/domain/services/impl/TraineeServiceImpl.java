package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.domain.services.base.AbstractUserService;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.repositories.TraineeRepository;
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
        entity.getUser().setFirstName(request.getFirstName());
        entity.getUser().setLastName(request.getLastName());
        entity.getUser().setIsActive(request.getIsActive());
        entity.setAddress(request.getAddress());
        entity.setDateOfBirth(request.getDateOfBirth());
    }

    @Override
    protected String getFullName(TraineeRequest request) {
        return String.format("%s %s",request.getFirstName(), request.getLastName());
    }
}

