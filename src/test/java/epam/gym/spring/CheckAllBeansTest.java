package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.domain.services.impl.TraineeServiceImpl;
import epam.gym.domain.services.impl.TrainerServiceImpl;
import epam.gym.domain.services.impl.TrainingServiceImpl;
import epam.gym.application.GymFacade;
import epam.gym.infrastructure.repositories.TraineeRepository;
import epam.gym.infrastructure.repositories.TrainerRepository;
import epam.gym.infrastructure.repositories.TrainingRepository;
import epam.gym.infrastructure.storage.StorageInitializer;
import epam.gym.infrastructure.storage.strategy.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class, StorageConfig.class})
class CheckAllBeansTest {
    private ApplicationContext applicationContext;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Test
    void testAllDaoBeansExist() {
        assertNotNull(applicationContext.getBean(TraineeRepository.class));
        assertNotNull(applicationContext.getBean(TrainerRepository.class));
        assertNotNull(applicationContext.getBean(TrainingRepository.class));
    }

    @Test
    void testAllServiceBeansExist() {
        assertNotNull(applicationContext.getBean(TraineeServiceImpl.class));
        assertNotNull(applicationContext.getBean(TrainerServiceImpl.class));
        assertNotNull(applicationContext.getBean(TrainingServiceImpl.class));
    }

    @Test
    void testFacadeBeanExists() {
        assertNotNull(applicationContext.getBean(GymFacade.class));
    }

    @Test
    void testStorageBeansExist() {
        assertNotNull(applicationContext.getBean("userStorage"));
        assertNotNull(applicationContext.getBean("traineeStorage"));
        assertNotNull(applicationContext.getBean("trainerStorage"));
        assertNotNull(applicationContext.getBean("trainingStorage"));
        assertNotNull(applicationContext.getBean("trainingTypeStorage"));
    }

    @Test
    void testUtilityBeansExist() {
        assertNotNull(applicationContext.getBean(StorageInitializer.class));
    }

    @Test
    void testDataLoaderBeansExist() {
        assertNotNull(applicationContext.getBean(UserDataLoader.class));
        assertNotNull(applicationContext.getBean(TraineeDataLoader.class));
        assertNotNull(applicationContext.getBean(TrainerDataLoader.class));
        assertNotNull(applicationContext.getBean(TrainingDataLoader.class));
        assertNotNull(applicationContext.getBean(TrainingTypeDataLoader.class));
    }

    @Test
    void testTotalBeanCount() {
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        assertTrue(beanNames.length > 20);
    }
}