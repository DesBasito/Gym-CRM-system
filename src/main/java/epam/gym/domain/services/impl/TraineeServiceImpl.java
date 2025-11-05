package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TraineeCreationRequest;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.infrastructure.repositories.impl.TraineeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;

    @Override
    public Trainee create(TraineeCreationRequest traineeCreationRequest) {
        log.info("Creating trainee: {} {}", traineeCreationRequest.getFirstName(), traineeCreationRequest.getLastName());


        Trainee trainee = new Trainee();
        trainee.setFirstName(traineeCreationRequest.getFirstName());
        trainee.setLastName(traineeCreationRequest.getLastName());
        trainee.setIsActive(true);
        trainee.setDateOfBirth(traineeCreationRequest.getDateOfBirth());
        trainee.setAddress(traineeCreationRequest.getAddress());

        Trainee createdTrainee = traineeRepository.save(trainee).orElse(null);

        assert createdTrainee != null;
        log.info("Trainee created successfully with userId: {}", createdTrainee.getUsername());

        return createdTrainee;
    }

    @Override
    public Trainee update(TraineeCreationRequest traineeCreationRequest, String username) {
        log.info("Updating trainee: {} {}", traineeCreationRequest.getFirstName(), traineeCreationRequest.getLastName());

        Trainee currentTrainee = traineeRepository.select(username).orElse(null);
        if (currentTrainee == null) {
            log.warn("Trainee with username {} not found", username);
            return null;
        }

        boolean nameChanged = !currentTrainee.getFirstName().equals(traineeCreationRequest.getFirstName()) ||
                              !currentTrainee.getLastName().equals(traineeCreationRequest.getLastName());

        if (nameChanged) {
            log.info("Name changed, deleting old trainee with username: {}", username);
            traineeRepository.delete(username);
        }

        Trainee trainee = Trainee.builder()
                .firstName(traineeCreationRequest.getFirstName())
                .lastName(traineeCreationRequest.getLastName())
                .dateOfBirth(traineeCreationRequest.getDateOfBirth())
                .address(traineeCreationRequest.getAddress())
                .isActive(traineeCreationRequest.getIsActive())
                .build();

        Trainee updatedTrainee = traineeRepository.save(trainee).orElse(null);

        if (updatedTrainee == null) {
            log.error("Failed to update trainee");
        } else {
            log.info("Trainee updated successfully with username: {}", updatedTrainee.getUsername());
        }

        return updatedTrainee;
    }

    @Override
    public Trainee select(String id) {
        log.info("Selecting trainee by userId: {}", id);

        Trainee trainee = traineeRepository.select(id).orElse(null);

        if (trainee == null) log.error("Trainee not found with userId: {}", id);
        else log.info("Trainee found with userId: {}", id);


        return trainee;
    }

    @Override
    public void delete(String id) {
        log.info("Deleting trainee with userId: {}", id);

        boolean deleted = traineeRepository.delete(id);

        if (deleted) log.info("Trainee deleted successfully with userId: {}", id);
        else log.error("Failed to delete trainee with userId: {}", id);

    }
}