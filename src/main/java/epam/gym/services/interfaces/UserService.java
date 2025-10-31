package epam.gym.services.interfaces;

import epam.gym.domain.entities.User;

public interface UserService {
    User create(String name, String surname);
    boolean update(User user);
    boolean delete(String id);
    boolean isExists(String id);
}
