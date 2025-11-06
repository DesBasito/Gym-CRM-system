package epam.gym.infrastructure.repositories.impl;

import epam.gym.infrastructure.dao.TraineeDao;
import epam.gym.infrastructure.dao.UserDao;
import epam.gym.infrastructure.repositories.EntityRepository;
import epam.gym.domain.entities.Trainee;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeRepository implements EntityRepository<Trainee, String> {
    private final Map<String, TraineeDao> traineeStorage;
    private final TraineeMapper traineeMapper;

    @Override
    public Trainee save(Trainee trainee) {
        if (trainee == null) {
            log.warn("Attempt to save null user");
            throw new IllegalArgumentException("Attempt to save null user");
        }

        String username = trainee.getUsername();

        if (username == null) {
            trainee.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                    trainee.getFirstName(),
                    trainee.getLastName()));
            checkDuplicate(trainee.getUsername());
            trainee.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
        } else {
            validateUpdate(username);
        }

        traineeStorage.put(trainee.getUsername(), traineeMapper.toDao(trainee));
        return trainee;
    }

    @Override
    public Trainee select(String id) {
        if (id == null) {
            log.warn("Attempt to select user with null id");
            throw new IllegalArgumentException("Attempt to select user with null id");
        }

        TraineeDao traineeDao = traineeStorage.get(id);
        return traineeMapper.toModel(traineeDao);
    }

    @Override
    public void delete(String id) {
        if (id == null) {
            log.warn("Attempt to delete user with null id");
            throw new IllegalArgumentException("Username can not be null!" );
        }

        if (!traineeStorage.containsKey(id)) {
            log.warn("Attempt to delete non-existent user with id {}", id);
            throw new IllegalArgumentException("Attempt to delete non-existent user with id: "+id);
        }

        traineeStorage.remove(id);
    }

    private void validateUpdate(String username) {
        boolean traineeExists = traineeStorage.containsKey(username);
        if (traineeExists) {
            log.error("User does not exists for username: {}", username);
            throw new NoSuchElementException("User by username: " + username + " not found!");
        }
    }

    private void checkDuplicate(String username) {
        if (traineeStorage.containsKey(username)) {
            log.error("User by username: {} already exists!", username);
            throw new IllegalStateException(
                    "User by username: " + username + " already exists!"
            );
        }
    }
}