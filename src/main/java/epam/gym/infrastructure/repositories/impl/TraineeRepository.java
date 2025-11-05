package epam.gym.infrastructure.repositories.impl;

import epam.gym.infrastructure.dao.TraineeDao;
import epam.gym.infrastructure.dao.UserDao;
import epam.gym.infrastructure.repositories.EntityRepository;
import epam.gym.domain.entities.Trainee;
import epam.gym.infrastructure.mappers.TraineeMapper;
import epam.gym.infrastructure.mappers.UserMapper;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeRepository implements EntityRepository<Trainee, String> {
    private final Map<String, TraineeDao> traineeStorage;
    private final Map<String, UserDao> userStorage;
    private final UserMapper userMapper;
    private final TraineeMapper traineeMapper;

    @Override
    public Optional<Trainee> save(Trainee entity) {
        if (entity == null) {
            log.warn("Attempt to save null trainee");
            return Optional.empty();
        }

        String username = entity.getUsername();

        if (username == null) {
            return saveNewTrainee(entity);
        } else {
            return updateExistingTrainee(entity, username);
        }
    }

    @Override
    public Optional<Trainee> select(String id) {
        if (id == null) {
            log.warn("Attempt to select trainee with null id");
            return Optional.empty();
        }

        TraineeDao traineeDao = traineeStorage.get(id);
        if (traineeDao == null) {
            log.warn("Trainee with id {} not found", id);
            return Optional.empty();
        }

        return Optional.of(traineeMapper.toModel(traineeDao));
    }

    @Override
    public boolean delete(String id) {
        if (id == null) {
            log.warn("Attempt to delete trainee with null id");
            return false;
        }

        if (!traineeStorage.containsKey(id)) {
            log.warn("Attempt to delete non-existent trainee with id {}", id);
            return false;
        }

        traineeStorage.remove(id);
        userStorage.remove(id);
        return true;
    }

    private Optional<Trainee> saveNewTrainee(Trainee entity) {
        entity.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                entity.getFirstName(),
                entity.getLastName()));
        checkDuplicate(entity.getUsername());
        entity.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());

        traineeStorage.put(entity.getUsername(), traineeMapper.toDao(entity));
        userStorage.put(entity.getUsername(), userMapper.toDao(entity));

        return Optional.of(entity);
    }

    private Optional<Trainee> updateExistingTrainee(Trainee entity, String oldUsername) {
        validateUpdate(oldUsername);

        UserDao userDao = userStorage.get(oldUsername);
        boolean nameChanged = !userDao.getFirstName().equals(entity.getFirstName()) ||
                              !userDao.getLastName().equals(entity.getLastName());

        if (nameChanged) {
            log.info("Name changed for trainee: {} -> {} {}", oldUsername, entity.getFirstName(), entity.getLastName());
            traineeStorage.remove(oldUsername);
            userStorage.remove(oldUsername);

            entity.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                    entity.getFirstName(),
                    entity.getLastName()));
            checkDuplicate(entity.getUsername());
            entity.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
        } else {
            entity.setUsername(oldUsername);
        }

        traineeStorage.put(entity.getUsername(), traineeMapper.toDao(entity));
        userStorage.put(entity.getUsername(), userMapper.toDao(entity));

        log.info("Trainee updated successfully with username: {}", entity.getUsername());
        return Optional.of(entity);
    }

    private void validateUpdate(String username) {
        boolean traineeExists = traineeStorage.containsKey(username);
        boolean userExists = userStorage.containsKey(username);

        if (traineeExists && !userExists) {
            log.error("Data inconsistency: Trainee exists but User does not for username: {}", username);
            throw new IllegalStateException(
                    "Data inconsistency: Trainee exists but User does not for username: " + username
            );
        }

        if (!traineeExists && userExists) {
            log.error("Data inconsistency: User exists but Trainee does not for username: {}", username);
            throw new IllegalStateException(
                    "Data inconsistency: User exists but Trainee does not for username: " + username
            );
        }
    }

    private void checkDuplicate(String username) {
        boolean traineeExists = traineeStorage.containsKey(username);
        boolean userExists = userStorage.containsKey(username);

        if (traineeExists && !userExists || !traineeExists && userExists) {
            log.error("Data inconsistency: Trainee exists but User does not, or vice verse for username: {}", username);
            throw new IllegalStateException(
                    "Trainee exists but User does not, or vice verse for username: " + username
            );
        }
    }
}