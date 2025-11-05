package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.Trainer;
import epam.gym.infrastructure.dao.UserDao;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserDao toDao(Trainer trainer) {
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer cannot be empty!");
        }

        return UserDao.builder()
                .username(trainer.getUsername())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .password(trainer.getPassword())
                .isActive(trainer.getIsActive())
                .build();
    }

    public UserDao toDao(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee cannot be empty!");
        }

        return UserDao.builder()
                .username(trainee.getUsername())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .password(trainee.getPassword())
                .isActive(trainee.getIsActive())
                .build();
    }
}
