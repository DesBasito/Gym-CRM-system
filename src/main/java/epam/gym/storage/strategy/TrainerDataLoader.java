package epam.gym.storage.strategy;

import epam.gym.domain.entities.Trainer;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Component
public class TrainerDataLoader implements DataLoader<Trainer, String>{

    @Override
    public void loadData(InputStream inputStream, Map<String, Trainer> storage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    Trainer trainer = new Trainer(
                            parts[0].trim(),
                            parts[1].trim()
                    );
                    storage.put(trainer.getUserId(), trainer);
                }
            }
        }
    }

    @Override
    public String getStorageBeanName() {
        return "trainerStorage";
    }
}
