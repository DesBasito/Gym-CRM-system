package epam.gym.dao.impl;

import epam.gym.dao.EntityDao;
import epam.gym.domain.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserDao implements EntityDao<User, String> {
    @Autowired
    private Map<String, User> userStorage;


    public boolean isUserNameExists(String generatedUsername) {
        return userStorage.get(generatedUsername) != null;
    }

    @Override
    public Optional<User> create(User user){
        return Optional.ofNullable(userStorage.put(user.getUsername(), user));
    }

    @Override
    public Optional<User> select(String id) {
        return Optional.of(userStorage.get(id));
    }

    @Override
    public Optional<User> update(User user) {
        return Optional.ofNullable(userStorage.put(user.getUsername(), user));
    }

    @Override
    public boolean delete(String s) {
        userStorage.remove(s);
        return userStorage.get(s) == null;
    }
}
