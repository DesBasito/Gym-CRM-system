package epam.gym.storage.strategy;

import epam.gym.domain.entities.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Component
public class UserDataLoader implements DataLoader<User,String>{

    @Override
    public void loadData(InputStream inputStream, Map<String, User> storage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    User user = new User(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim(),
                            parts[3].trim(),
                            Boolean.parseBoolean(parts[4].trim())
                    );
                    storage.put(user.getUsername(), user);
                }
            }
        }
    }

    @Override
    public String getStorageBeanName() {
        return "userStorage";
    }
}
