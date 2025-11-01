package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements EntityDao<User, String> {
    @Autowired
    private Map<String, User> userStorage;

    public boolean isUserNameExists(String generatedUsername) {
        return userStorage.containsKey(generatedUsername);
    }

    @Override
    public Optional<User> create(User user){
        if (user == null || user.getUsername() == null) {
            return Optional.empty();
        }
        userStorage.put(user.getUsername(), user);
        return Optional.of(user);
    }

    @Override
    public Optional<User> select(String id) {
        return Optional.ofNullable(userStorage.get(id));
    }

    @Override
    public Optional<User> update(User user) {
        if (user == null || user.getUsername() == null || !userStorage.containsKey(user.getUsername())) {
            return Optional.empty();
        }
        userStorage.put(user.getUsername(), user);
        return Optional.of(user);
    }

    @Override
    public boolean delete(String s) {
        return userStorage.remove(s) != null;
    }
}