package epam.gym.domain.services.impl;

import epam.gym.domain.dto.request.TrainerCreationRequest;
import epam.gym.domain.entities.Trainer;
import epam.gym.domain.services.interfaces.TrainerService;
import epam.gym.infrastructure.repositories.impl.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

        Trainer createdTrainer = trainerRepository.save(trainer).orElse(null);

        assert createdTrainer != null;
        log.info("Trainer created successfully with username: {}", createdTrainer.getUsername());

        return createdTrainer;
    }

    @Override
    public Trainer update(TrainerCreationRequest trainerCreationRequest, String username) {
        log.info("Updating trainer: {} {}", trainerCreationRequest.getFirstName(), trainerCreationRequest.getLastName());

        Trainer currentTrainer = trainerRepository.select(username).orElse(null);
        if (currentTrainer == null) {
            log.warn("Trainer with username {} not found", username);
            return null;
        }

        boolean nameChanged = !currentTrainer.getFirstName().equals(trainerCreationRequest.getFirstName()) ||
                              !currentTrainer.getLastName().equals(trainerCreationRequest.getLastName());

        if (nameChanged) {
            log.info("Name changed, deleting old trainer with username: {}", username);
            trainerRepository.delete(username);
        }

        Trainer trainer = Trainer.builder()
                .firstName(trainerCreationRequest.getFirstName())
                .lastName(trainerCreationRequest.getLastName())
                .isActive(trainerCreationRequest.getIsActive())
                .specialization(trainerCreationRequest.getSpecialization())
                .build();

        Trainer updatedTrainer = trainerRepository.save(trainer).orElse(null);

        if (updatedTrainer == null) {
            log.error("Failed to update trainer");
        } else {
            log.info("Trainer updated successfully with username: {}", updatedTrainer.getUsername());
        }

        return updatedTrainer;
    }

    @Override
    public Trainer select(String id) {
        log.info("Selecting trainer by username: {}", id);

        Trainer trainer = trainerRepository.select(id).orElse(null);

        if (trainer == null) {
            log.error("Trainer not found with username: {}", id);
        } else {
            log.info("Trainer found with username: {}", id);
        }

        return trainer;
    }
}