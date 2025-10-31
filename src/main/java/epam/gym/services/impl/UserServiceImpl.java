package epam.gym.services.impl;

import epam.gym.dao.impl.UserDao;
import epam.gym.domain.dto.TraineeDto;
import epam.gym.domain.entities.Trainee;
import epam.gym.domain.entities.User;
import epam.gym.services.interfaces.UserService;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UsernameAndPasswordGenerator usernameAndPasswordGenerator;

    @Override
    public User create(String name, String surname) {
        log.info("Creating user: {} {}", name, surname);

        String username = usernameAndPasswordGenerator.generateAndGetUsername(name, surname);
        if (usernameAndPasswordGenerator.checkForDuplicate(username)){
            log.error("Failed to create user: username {} already exists", username);
            return null;
        }
        String password = usernameAndPasswordGenerator.generateAndGetPassword();

        User user = new User();
        user.setFirstName(name);
        user.setLastName(surname);
        user.setUsername(username);
        user.setPassword(password);
        user.setIsActive(true);

        log.info("User created successfully with username: {}", username);
        return userDao.create(user).get();
    }

    @Override
    public boolean update(User updateUser) {
        log.info("Updating user: {}", updateUser.getUsername());

        User user = new User();
        user.setFirstName(updateUser.getFirstName());
        user.setLastName(updateUser.getLastName());
        user.setUsername(updateUser.getUsername());
        user.setPassword(updateUser.getPassword());
        user.setIsActive(updateUser.getIsActive());

        boolean updated = userDao.update(user).isPresent();

        if (updated) {
            log.info("User updated successfully: {}", updateUser.getUsername());
        } else {
            log.error("Failed to update user: {}", updateUser.getUsername());
        }

        return updated;
    }

    @Override
    public boolean delete(String id) {
        log.info("Deleting user: {}", id);

        boolean deleted = userDao.delete(id);

        if (deleted) {
            log.info("User deleted successfully: {}", id);
        } else {
            log.error("Failed to delete user: {}", id);
        }

        return deleted;
    }

    @Override
    public boolean isExists(String id) {
        log.debug("Checking if user exists: {}", id);
        return userDao.isUserNameExists(id);
    }
}