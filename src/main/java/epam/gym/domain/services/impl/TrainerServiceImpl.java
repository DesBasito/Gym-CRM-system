package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainerCreationRequest;
import epam.gym.domain.entities.Trainer;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.repositories.impl.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerRepository trainerRepository;

    @Override
    public Trainer create(TrainerCreationRequest trainerCreationRequest) {
        log.info("Creating trainer: {} {}", trainerCreationRequest.getFirstName(), trainerCreationRequest.getLastName());

        Trainer trainer = new Trainer();
        trainer.setFirstName(trainerCreationRequest.getFirstName());
        trainer.setLastName(trainerCreationRequest.getLastName());
        trainer.setIsActive(true);
        trainer.setSpecialization(trainerCreationRequest.getSpecialization());

        Trainer createdTrainer = trainerRepository.save(trainer);

        log.info("Trainer created successfully with username: {}", createdTrainer.getUsername());
        return createdTrainer;
    }

    @Override
    public Trainer update(TrainerCreationRequest trainerCreationRequest, String username) {
        log.info("Updating user: {} {}", trainerCreationRequest.getFirstName(), trainerCreationRequest.getLastName());

        Trainer currentTrainer = trainerRepository.select(username);
        if (currentTrainer == null) {
            log.warn("User with username {} not found", username);
            throw new NoSuchElementException("User with username "+username+" not found");
        }

        boolean nameChanged = !currentTrainer.getFirstName().equals(trainerCreationRequest.getFirstName()) ||
                              !currentTrainer.getLastName().equals(trainerCreationRequest.getLastName());

        Trainer trainer = Trainer.builder()
                .firstName(trainerCreationRequest.getFirstName())
                .lastName(trainerCreationRequest.getLastName())
                .isActive(trainerCreationRequest.getIsActive())
                .specialization(trainerCreationRequest.getSpecialization())
                .build();

        if (nameChanged) {
            log.info("Name changed, deleting old user with username: {}", username);
            trainerRepository.delete(username);
        } else {
            trainer.setUsername(username);
        }


        Trainer updatedTrainer = trainerRepository.save(trainer);

        if (updatedTrainer == null) {
            log.error("Failed to update user");
        } else {
            log.info("User updated successfully with username: {}", updatedTrainer.getUsername());
        }

        return updatedTrainer;
    }

    @Override
    public Trainer select(String id) {
        log.info("Selecting user by username: {}", id);

        Trainer trainer = trainerRepository.select(id);

        if (trainer == null) {
            log.error("User not found with username: {}", id);
        } else {
            log.info("User found with username: {}", id);
        }

        return trainer;
    }
}