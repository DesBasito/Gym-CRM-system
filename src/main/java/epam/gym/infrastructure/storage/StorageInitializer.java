package epam.gym.infrastructure.storage;

import epam.gym.infrastructure.storage.strategy.DataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StorageInitializer implements InitializingBean, ApplicationContextAware {

    @Value("${storage.users}")
    private String usersFilePath;

    @Value("${storage.trainers}")
    private String trainersFilePath;

    @Value("${storage.trainees}")
    private String traineesFilePath;

    @Value("${storage.trainings}")
    private String trainingsFilePath;

    @Value("${storage.trainingTypes}")
    private String trainingTypesFilePath;

    private final List<DataLoader<?, ?>> dataLoaders;
    private ApplicationContext applicationContext;
    private Map<String, String> filePathMap;

    public StorageInitializer(List<DataLoader<?, ?>> dataLoaders) {
        this.dataLoaders = dataLoaders;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterPropertiesSet() {
        filePathMap = new HashMap<>();
        filePathMap.put("userStorage", usersFilePath);
        filePathMap.put("trainerStorage", trainersFilePath);
        filePathMap.put("traineeStorage", traineesFilePath);
        filePathMap.put("trainingStorage", trainingsFilePath);
        filePathMap.put("trainingTypeStorage", trainingTypesFilePath);

        for (DataLoader<?, ?> loader : dataLoaders) {
            String beanName = loader.getStorageBeanName();
            String filePath = filePathMap.get(beanName);

            if (filePath != null && applicationContext.containsBean(beanName)) {
                Map<Object, Object> storage = (Map<Object, Object>) applicationContext.getBean(beanName);
                loadData(loader, storage, filePath, beanName);
            }
        }

        log.info("All storages initialized successfully");
    }

    @SuppressWarnings("unchecked")
    private <V, ID> void loadData(DataLoader<V, ID> loader, Map<Object, Object> storage,
                                  String filePath, String beanName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                log.warn("File not found: {}", filePath);
                return;
            }

            Map<ID, V> loadedData = loader.loadData(is);
            if (loadedData != null && !loadedData.isEmpty()) {
                storage.putAll((Map<? extends Object, ? extends Object>) loadedData);
                log.info("Loaded {} entries for {}", loadedData.size(), beanName);
            } else {
                log.warn("No data loaded from {}", filePath);
            }
        } catch (Exception e) {
            log.error("Error loading data for {}: {}", beanName, e.getMessage(), e);
        }
    }
}