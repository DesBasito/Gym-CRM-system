package epam.gym.storage.strategy;

import epam.gym.domain.entities.TrainingType;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

@Component
public class TrainingTypeDataLoader implements DataLoader<TrainingType,String>{

    @Override
    public void loadData(InputStream inputStream, Map<String, TrainingType> storage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1) {
                    TrainingType trainingType = new TrainingType(parts[0].trim());
                    storage.put(trainingType.getTrainingTypeName(), trainingType);
                }
            }
        }
    }

    @Override
    public String getStorageBeanName() {
        return "trainingTypeStorage";
    }
}
