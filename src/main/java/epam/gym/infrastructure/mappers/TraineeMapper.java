package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.User;
import epam.gym.infrastructure.dao.TraineeDao;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
@RequiredArgsConstructor
public class TraineeMapper {
    private final Map<String, UserDao> userStorage;

    public TraineeDao toDao(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee cannot be empty!");
        }

        TraineeDao traineeDao = new TraineeDao();
        traineeDao.setUsername(trainee.getUsername());
        traineeDao.setDateOfBirth(trainee.getDateOfBirth());
        traineeDao.setAddress(trainee.getAddress());

        return traineeDao;
    }

    public Trainee toModel(TraineeDao traineeDao) {
        if (traineeDao == null) {
            throw new IllegalArgumentException("Trainee cannot be empty!");
        }

        UserDao userDao = userStorage.get(traineeDao.getUsername());
        if (userDao == null) {
            throw new IllegalArgumentException("User not found!");
        }

        return Trainee.builder()
                .username(userDao.getUsername())
                .firstName(userDao.getFirstName())
                .lastName(userDao.getLastName())
                .password(userDao.getPassword())
                .isActive(userDao.getIsActive())
                .dateOfBirth(traineeDao.getDateOfBirth())
                .address(traineeDao.getAddress())
                .build();
    }
}
