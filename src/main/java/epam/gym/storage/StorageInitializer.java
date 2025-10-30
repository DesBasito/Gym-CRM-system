package epam.gym.storage;

import epam.gym.storage.strategy.DataLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StorageInitializer implements BeanPostProcessor, InitializingBean {

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

    private List<DataLoader<?, ?>> dataLoaders;
    private boolean initialized = false;
    private Map<String, String> filePathMap;

    @Autowired
    public void setDataLoaders(List<DataLoader<?, ?>> dataLoaders) {
        this.dataLoaders = dataLoaders;
    }

    @Override
    public void afterPropertiesSet() {
        filePathMap = new HashMap<>();
        filePathMap.put("userStorage", usersFilePath);
        filePathMap.put("trainerStorage", trainersFilePath);
        filePathMap.put("traineeStorage", traineesFilePath);
        filePathMap.put("trainingStorage", trainingsFilePath);
        filePathMap.put("trainingTypeStorage", trainingTypesFilePath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!initialized && bean instanceof Map) {
            for (DataLoader<?, ?> loader : dataLoaders) {
                if (loader.getStorageBeanName().equals(beanName)) {
                    String filePath = filePathMap.get(beanName);
                    loadData(loader, (Map<?, ?>) bean, filePath, beanName);
                    break;
                }
            }

            if (beanName.equals("trainingTypeStorage")) {
                initialized = true;
            }
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private <T, V> void loadData(DataLoader<T, V> loader, Map<?, ?> storage, String filePath, String beanName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("File not found: {}", filePath);
                return;
            }
            loader.loadData(is, (Map<V, T>) storage);
            log.info("Loaded {} entries for {}", storage.size(), beanName);
        } catch (Exception e) {
            log.error("Error loading data for {}: {}", beanName, e.getMessage());
        }
    }

}
