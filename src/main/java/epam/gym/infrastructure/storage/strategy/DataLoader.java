package epam.gym.infrastructure.storage.strategy;

import java.io.InputStream;
import java.util.Map;

public interface DataLoader<V, ID> {
    Map<ID, V> loadData(InputStream inputStream);
    String getStorageBeanName();
}
