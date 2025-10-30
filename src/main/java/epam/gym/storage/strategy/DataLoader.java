package epam.gym.storage.strategy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface DataLoader<T, V> {
    void loadData(InputStream inputStream, Map<V, T> storage) throws IOException;
    String getStorageBeanName();
}
