package epam.gym.infrastructure.storage.strategy;

import epam.gym.infrastructure.dao.TrainingTypeDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class TrainingTypeDataLoader implements DataLoader<TrainingTypeDao, String> {

    @Override
    public Map<String, TrainingTypeDao> loadData(InputStream inputStream) {
        Map<String, TrainingTypeDao> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    TrainingTypeDao trainingType = new TrainingTypeDao();
                    trainingType.setTrainingTypeName(line);

                    result.put(trainingType.getTrainingTypeName(), trainingType);
                }
            }
        } catch (IOException e) {
            log.error("Error loading training types: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public String getStorageBeanName() {
        return "trainingTypeStorage";
    }
}