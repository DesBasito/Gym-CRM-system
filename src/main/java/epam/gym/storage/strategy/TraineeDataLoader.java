package epam.gym.storage.strategy;

import epam.gym.domain.entities.Trainee;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Map;

@Component
public class TraineeDataLoader implements DataLoader<Trainee, String>{

    @Override
    public void loadData(InputStream inputStream, Map<String, Trainee> storage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    Trainee trainee = new Trainee(
                            LocalDate.parse(parts[0].trim()),
                            parts[1].trim(),
                            parts[2].trim()
                    );
                    storage.put(trainee.getUserId(), trainee);
                }
            }
        }
    }

    @Override
    public String getStorageBeanName() {
        return "traineeStorage";
    }
}
