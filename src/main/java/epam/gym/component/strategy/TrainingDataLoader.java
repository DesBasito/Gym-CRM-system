package epam.gym.component.strategy;

import epam.gym.entities.EmbeddedTrainingId;
import epam.gym.entities.Training;
import epam.gym.entities.User;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Map;

@Component
public class TrainingDataLoader implements DataLoader<Training>{
    @Override
    public void loadData(InputStream inputStream, Map<String, Training> storage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    EmbeddedTrainingId eti = new EmbeddedTrainingId(
                            parts[0].trim(),
                            parts[1].trim(),
                            parts[2].trim()
                    );
                    Training training = new Training(
                            eti,
                            parts[3].trim(),
                            LocalDate.parse(parts[4].trim()),
                            parts[5].trim()
                            );
                    storage.put(training.getTrainingId().toString(), training);
                }
            }
        }
    }

    @Override
    public String getStorageBeanName() {
        return "";
    }
}
