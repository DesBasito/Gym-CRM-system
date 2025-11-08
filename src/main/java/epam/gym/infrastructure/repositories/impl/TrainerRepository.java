package epam.gym.infrastructure.repositories.impl;

import epam.gym.domain.entities.Trainer;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import epam.gym.infrastructure.repositories.EntityRepository;
import epam.gym.infrastructure.mappers.TrainerMapper;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainerRepository implements EntityRepository<Trainer, String> {
    private final Map<String, TrainerDao> trainerStorage;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer save(Trainer trainer) {
        if (trainer == null) {
            log.warn("Attempt to save null user");
            throw new IllegalArgumentException("Attempt to save null user");
        }

        String username = trainer.getUsername();

        if (username == null) {
            trainer.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(
                    trainer.getFirstName(),
                    trainer.getLastName()));
            checkDuplicate(trainer.getUsername());
            trainer.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
        } else {
            validateUpdate(username);
        }

        trainerStorage.put(trainer.getUsername(), trainerMapper.toDao(trainer));
        return trainer;
    }
    @Override
    public Trainer select(String id) {
        if (id == null) {
            log.warn("Attempt to select user with null id");
            throw new IllegalArgumentException("Attempt to select user with null id");
        }

        TrainerDao trainerDao = trainerStorage.get(id);
        if (trainerDao == null) {
            log.warn("User with id {} not found", id);
            throw new NoSuchElementException("User with id: " + id + " not found");
        }

        return trainerMapper.toModel(trainerDao);
    }

    private void validateUpdate(String username) {
        boolean trainerExists = !trainerStorage.containsKey(username);
        if (trainerExists) {
            log.error("User does not exists for username: {}", username);
            throw new NoSuchElementException("User by username: " + username + " not found!");
        }
    }

    private void checkDuplicate(String username) {
        if (trainerStorage.containsKey(username)) {
            log.error("User by username: {} already exists!", username);
            throw new IllegalStateException(
                    "User by username: " + username + " already exists!"
            );
        }
    }
}