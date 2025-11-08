package epam.gym.infrastructure.storage.strategy;

import epam.gym.infrastructure.dao.EmbeddedTrainingDaoId;
import epam.gym.infrastructure.dao.TrainingDao;
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
public class TrainingDataLoader implements DataLoader<TrainingDao, EmbeddedTrainingDaoId> {

    @Override
    public Map<EmbeddedTrainingDaoId, TrainingDao> loadData(InputStream inputStream) {
        Map<EmbeddedTrainingDaoId, TrainingDao> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    String[] parts = line.split(",");

                    if (parts.length == 6) {
                        EmbeddedTrainingDaoId id = new EmbeddedTrainingDaoId(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim()
                        );

                        TrainingDao training = new TrainingDao();
                        training.setTrainingDaoId(id);
                        training.setTrainingType(parts[3].trim());
                        training.setTrainingDate(LocalDate.parse(parts[4].trim()));
                        training.setTrainingDuration(parts[5].trim());

                        result.put(id, training);
                    } else {
                        log.warn("Invalid training line: {}", line);
                    }
                }
            }
        } catch (IOException | DateTimeParseException e) {
            log.error("Error loading trainings: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public String getStorageBeanName() {
        return "trainingStorage";
    }
}