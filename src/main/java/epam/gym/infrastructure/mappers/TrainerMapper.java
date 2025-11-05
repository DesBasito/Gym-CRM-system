package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Trainer;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrainerMapper {
    private final Map<String, UserDao> userStorage;

    public TrainerDao toDao(Trainer trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainer cannot be empty!");
        }

        TrainerDao traineeDao = new TrainerDao();
        traineeDao.setUsername(trainee.getUsername());
        traineeDao.setSpecialization(trainee.getSpecialization());

        return traineeDao;
    }

    public Trainer toModel(TrainerDao traineeDao) {
        if (traineeDao == null) {
            throw new IllegalArgumentException("Trainer cannot be empty!");
        }

        UserDao userDao = userStorage.get(traineeDao.getUsername());
        if (userDao == null) {
            throw new IllegalArgumentException("User not found!");
        }

        return Trainer.builder()
                .username(userDao.getUsername())
                .firstName(userDao.getFirstName())
                .lastName(userDao.getLastName())
                .password(userDao.getPassword())
                .isActive(userDao.getIsActive())
                .specialization(traineeDao.getSpecialization())
                .build();
    }
}
