package epam.gym.services.impl;

import epam.gym.dao.impl.TrainerDao;
import epam.gym.domain.dto.TrainerDto;
import epam.gym.domain.entities.Trainer;
import epam.gym.domain.entities.User;
import epam.gym.services.interfaces.TrainerService;
import epam.gym.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    @Autowired
    private TrainerDao trainerDao;

    @Autowired
    private UserService userService;

    @Override
    public Trainer create(TrainerDto trainerDto) {
        log.info("Creating trainer: {} {}", trainerDto.getFirstName(), trainerDto.getLastName());

        User created = userService.create(trainerDto.getFirstName(), trainerDto.getLastName());
        if (created == null) {
            log.error("Failed to create trainer: user creation failed for {} {}",
                    trainerDto.getFirstName(), trainerDto.getLastName());
            return null;
        }

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setUserId(created.getUsername());

        Trainer createdTrainer = trainerDao.create(trainer).orElse(null);

        if (createdTrainer == null) log.error("Failed to create trainer with userId: {}", created.getUsername());
         else log.info("Trainer created successfully with userId: {}", createdTrainer.getUserId());

        return createdTrainer;
    }

    @Override
    public Trainer update(TrainerDto trainerDto, String username) {
        log.info("Updating trainer: {} {}", trainerDto.getFirstName(), trainerDto.getLastName());

        boolean exists = userService.isExists(trainerDto.getFirstName()+'.'+trainerDto.getLastName());
        String newUsername = username;
        if (!exists) {
            log.info("Username changed, deleting old user: {}", username);
            userService.delete(username);

            User user = userService.create(trainerDto.getFirstName(), trainerDto.getLastName());
            if (user == null) {
                log.error("Failed to update trainer: user creation failed for {} {}",
                        trainerDto.getFirstName(), trainerDto.getLastName());
                return null;
            }
            newUsername = user.getUsername();
        }

        Trainer trainer = new Trainer();
        trainer.setSpecialization(trainerDto.getSpecialization());
        trainer.setUserId(newUsername);

        Trainer updatedTrainer = trainerDao.update(trainer).orElse(null);

        if (updatedTrainer == null) log.error("Failed to update trainer with userId: {}", newUsername);
         else log.info("Trainer updated successfully with userId: {}", updatedTrainer.getUserId());


        return updatedTrainer;
    }

    @Override
    public Trainer select(String id) {
        log.info("Selecting trainer by userId: {}", id);

        Trainer trainer = trainerDao.select(id).orElse(null);

        if (trainer == null) log.error("Trainer not found with userId: {}", id);
         else log.info("Trainer found with userId: {}", id);


        return trainer;
    }
}