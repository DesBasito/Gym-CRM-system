package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TraineeCreationRequest;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.services.interfaces.TraineeService;
import epam.gym.infrastructure.repositories.impl.TraineeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;

    @Override
    public Trainee create(TraineeCreationRequest traineeCreationRequest) {
        log.info("Creating user: {} {}", traineeCreationRequest.getFirstName(), traineeCreationRequest.getLastName());


        Trainee trainee = new Trainee();
        trainee.setFirstName(traineeCreationRequest.getFirstName());
        trainee.setLastName(traineeCreationRequest.getLastName());
        trainee.setIsActive(true);
        trainee.setDateOfBirth(traineeCreationRequest.getDateOfBirth());
        trainee.setAddress(traineeCreationRequest.getAddress());

        Trainee createdTrainee = traineeRepository.save(trainee);
        log.info("User created successfully with userId: {}", createdTrainee.getUsername());
        return createdTrainee;
    }

    @Override
    public Trainee update(TraineeCreationRequest traineeCreationRequest, String username) {
        log.info("Updating user: {} {}", traineeCreationRequest.getFirstName(), traineeCreationRequest.getLastName());
        Trainee currentTrainee = traineeRepository.select(username);
        if (currentTrainee == null) {
            log.warn("User with username {} not found", username);
            throw new IllegalArgumentException("User with username: " + username + " not found!");
        }

        boolean nameChanged = !currentTrainee.getFirstName().equals(traineeCreationRequest.getFirstName()) ||
                              !currentTrainee.getLastName().equals(traineeCreationRequest.getLastName());

        Trainee trainee = Trainee.builder()
                .firstName(traineeCreationRequest.getFirstName())
                .lastName(traineeCreationRequest.getLastName())
                .dateOfBirth(traineeCreationRequest.getDateOfBirth())
                .address(traineeCreationRequest.getAddress())
                .isActive(traineeCreationRequest.getIsActive())
                .build();

        if (!nameChanged) {
            log.info("Name changed, deleting old user with username: {}", username);
            traineeRepository.delete(username);
        } else {
            trainee.setUsername(username);
        }

        Trainee updatedTrainee = traineeRepository.save(trainee);
        log.info("User updated successfully with username: {}", updatedTrainee.getUsername());
        return updatedTrainee;
    }

    @Override
    public Trainee select(String id) {
        log.info("Selecting user by userId: {}", id);
        Trainee trainee = traineeRepository.select(id);

        if (trainee == null){
            throw new NoSuchElementException("User by username: "+id+" not found!");
        }

        log.info("User found with userId: {}", id);
        return trainee;
    }

    @Override
    public void delete(String id) {
        log.info("Deleting user with userId: {}", id);
        traineeRepository.delete(id);
        log.info("User deleted successfully with userId: {}", id);
    }
}