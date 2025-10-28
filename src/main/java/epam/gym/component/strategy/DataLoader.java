package epam.gym.component.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DataLoader<T> {
    void loadData(InputStream inputStream, Map<String, T> storage) throws IOException;
    String getStorageBeanName();
}
