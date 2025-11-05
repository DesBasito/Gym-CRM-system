package epam.gym.infrastructure.repositories.impl;

import epam.gym.domain.entities.Trainer;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import epam.gym.infrastructure.repositories.EntityRepository;
import epam.gym.infrastructure.mappers.TrainerMapper;
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
public class TrainerRepository implements EntityRepository<Trainer, String> {
    private final Map<String, TrainerDao> trainerStorage;
    private final Map<String, UserDao> userStorage;
    private final UserMapper userMapper;
    private final TrainerMapper trainerMapper;

    @Override
    public Optional<Trainer> save(Trainer entity) {
        if (entity == null) {
            log.warn("Attempt to save null trainer");
            return Optional.empty();
        }

        String username = entity.getUsername();

        if (username == null) {
            return saveNewTrainer(entity);
        } else {
            return updateExistingTrainer(entity, username);
        }
    }

    @Override
    public Optional<Trainer> select(String id) {
        if (id == null) {
            log.warn("Attempt to select trainer with null id");
            return Optional.empty();
        }

        TrainerDao trainerDao = trainerStorage.get(id);
        if (trainerDao == null) {
            log.warn("Trainer with id {} not found", id);
            return Optional.empty();
        }

        return Optional.of(trainerMapper.toModel(trainerDao));
    }

    private Optional<Trainer> saveNewTrainer(Trainer entity) {
        entity.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                entity.getFirstName(),
                entity.getLastName()));
        checkDuplicate(entity.getUsername());
        entity.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());

        trainerStorage.put(entity.getUsername(), trainerMapper.toDao(entity));
        userStorage.put(entity.getUsername(), userMapper.toDao(entity));

        return Optional.of(entity);
    }

    private Optional<Trainer> updateExistingTrainer(Trainer entity, String oldUsername) {
        validateUpdate(oldUsername);

        UserDao currentTrainerDao = userStorage.get(oldUsername);

        boolean nameChanged = !currentTrainerDao.getFirstName().equals(entity.getFirstName()) ||
                              !currentTrainerDao.getLastName().equals(entity.getLastName());

        if (nameChanged) {
            log.info("Name changed for trainer: {} -> {} {}", oldUsername, entity.getFirstName(), entity.getLastName());
            trainerStorage.remove(oldUsername);
            userStorage.remove(oldUsername);

            entity.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                    entity.getFirstName(),
                    entity.getLastName()));
            checkDuplicate(entity.getUsername());
            entity.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
        } else {
            entity.setUsername(oldUsername);
        }

        trainerStorage.put(entity.getUsername(), trainerMapper.toDao(entity));
        userStorage.put(entity.getUsername(), userMapper.toDao(entity));

        log.info("Trainer updated successfully with username: {}", entity.getUsername());
        return Optional.of(entity);
    }

    private void validateUpdate(String username) {
        boolean trainerExists = trainerStorage.containsKey(username);
        boolean userExists = userStorage.containsKey(username);

        if (trainerExists && !userExists) {
            log.error("Data inconsistency: Trainer exists but User does not for username: {}", username);
            throw new IllegalStateException(
                    "Data inconsistency: Trainer exists but User does not for username: " + username
            );
        }

        if (!trainerExists && userExists) {
            log.error("Data inconsistency: User exists but Trainer does not for username: {}", username);
            throw new IllegalStateException(
                    "Data inconsistency: User exists but Trainer does not for username: " + username
            );
        }
    }

    private void checkDuplicate(String username) {
        boolean trainerExists = trainerStorage.containsKey(username);
        boolean userExists = userStorage.containsKey(username);

        if (trainerExists && !userExists || !trainerExists && userExists) {
            log.error("Data inconsistency: Trainer exists but User does not, or vice verse for username: {}", username);
            throw new IllegalStateException(
                    "Trainer exists but User does not, or vice verse for username: " + username
            );
        }
    }
}