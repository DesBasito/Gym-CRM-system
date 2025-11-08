package epam.gym.infrastructure.storage.strategy;


import epam.gym.infrastructure.dao.TrainerDao;
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
public class TrainerDataLoader implements DataLoader<TrainerDao, String> {

    @Override
    public Map<String, TrainerDao> loadData(InputStream inputStream) {
        Map<String, TrainerDao> result = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (!line.isEmpty()) {
                    String[] parts = line.split(",");

                    if (parts.length == 2) {
                        TrainerDao trainer = new TrainerDao();
                        trainer.setSpecialization(parts[0].trim());
                        trainer.setUsername(parts[1].trim());

                        result.put(trainer.getUsername(), trainer);
                    } else {
                        log.warn("Invalid trainer line: {}", line);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error loading trainers: {}", e.getMessage());
        }

        return result;
    }

    @Override
    public String getStorageBeanName() {
        return "trainerStorage";
    }
}