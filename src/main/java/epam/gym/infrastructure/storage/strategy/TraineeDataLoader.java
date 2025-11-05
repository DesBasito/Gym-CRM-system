package epam.gym.infrastructure.storage.strategy;

import epam.gym.domain.entities.Trainee;
import epam.gym.infrastructure.dao.TraineeDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TraineeDataLoader implements DataLoader<TraineeDao, String> {

    @Override
    public Map<String, TraineeDao> loadData(InputStream inputStream) {
        Map<String, TraineeDao> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    String[] parts = line.split(",");

                    if (parts.length == 3) {
                        TraineeDao trainee = new TraineeDao();
                        trainee.setDateOfBirth(LocalDate.parse(parts[0].trim()));
                        trainee.setAddress(parts[1].trim());
                        trainee.setUsername(parts[2].trim());

                        result.put(trainee.getUsername(), trainee);
                    } else {
                        log.warn("Invalid trainee line: {}", line);
                    }
                }
            }
        } catch (IOException | DateTimeParseException e) {
            log.error("Error loading trainees: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public String getStorageBeanName() {
        return "traineeStorage";
    }
}
