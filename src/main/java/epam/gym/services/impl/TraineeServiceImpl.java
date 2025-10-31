package epam.gym.services.impl;

import epam.gym.dao.impl.TraineeDao;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.User;
import epam.gym.services.interfaces.TraineeService;
import epam.gym.services.interfaces.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    @Autowired
    private TraineeDao traineeDao;

    @Autowired
    private UserService userService;

    @Override
    public Trainee create(TraineeDto traineeDto) {
        log.info("Creating trainee: {} {}", traineeDto.getFirstName(), traineeDto.getLastName());

        User created = userService.create(traineeDto.getFirstName(), traineeDto.getLastName());
        if (created == null) {
            log.error("Failed to create trainee: user creation failed for {} {}",
                    traineeDto.getFirstName(), traineeDto.getLastName());
            return null;
        }

        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress());
        trainee.setUserId(created.getUsername());

        Trainee createdTrainee = traineeDao.create(trainee).orElse(null);

        if (createdTrainee == null) log.error("Failed to create trainee with userId: {}", created.getUsername());
        else log.info("Trainee created successfully with userId: {}", createdTrainee.getUserId());


        return createdTrainee;
    }

    @Override
    public Trainee update(TraineeDto traineeDto, String username) {
        log.info("Updating trainee: {} {}", traineeDto.getFirstName(), traineeDto.getLastName());

        boolean exists = userService.isExists(traineeDto.getFirstName() + '.' + traineeDto.getLastName());
        String newUsername = username;
        if (!exists) {
            log.info("Username changed, deleting old user: {}", username);
            userService.delete(username);

            User user = userService.create(traineeDto.getFirstName(), traineeDto.getLastName());
            if (user == null) {
                log.error("Failed to update trainee: user creation failed for {} {}",
                        traineeDto.getFirstName(), traineeDto.getLastName());
                return null;
            }
            newUsername = user.getUsername();
        }

        Trainee trainee = new Trainee();
        trainee.setDateOfBirth(traineeDto.getDateOfBirth());
        trainee.setAddress(traineeDto.getAddress());
        trainee.setUserId(newUsername);

        Trainee updatedTrainee = traineeDao.update(trainee).orElse(null);

        if (updatedTrainee == null) log.error("Failed to update trainee with userId: {}", newUsername);
        else log.info("Trainee updated successfully with userId: {}", updatedTrainee.getUserId());


        return updatedTrainee;
    }

    @Override
    public Trainee select(String id) {
        log.info("Selecting trainee by userId: {}", id);

        Trainee trainee = traineeDao.select(id).orElse(null);

        if (trainee == null) log.error("Trainee not found with userId: {}", id);
        else log.info("Trainee found with userId: {}", id);


        return trainee;
    }

    @Override
    public void delete(String id) {
        log.info("Deleting trainee with userId: {}", id);

        boolean deleted = traineeDao.delete(id);

        if (deleted) log.info("Trainee deleted successfully with userId: {}", id);
        else log.error("Failed to delete trainee with userId: {}", id);

    }
}