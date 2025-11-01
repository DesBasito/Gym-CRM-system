package epam.gym.spring;

import epam.gym.config.ApplicationConfig;
import epam.gym.config.StorageConfig;
import epam.gym.dao.impl.*;
import epam.gym.facade.GymFacade;
import epam.gym.services.impl.*;
import epam.gym.storage.StorageInitializer;
import epam.gym.storage.strategy.*;
import epam.gym.util.UsernameAndPasswordGenerator;
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

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void testAllDaoBeansExist() {
        assertNotNull(applicationContext.getBean(UserDao.class));
        assertNotNull(applicationContext.getBean(TraineeDao.class));
        assertNotNull(applicationContext.getBean(TrainerDao.class));
        assertNotNull(applicationContext.getBean(TrainingDao.class));
    }

    @Test
    void testAllServiceBeansExist() {
        assertNotNull(applicationContext.getBean(UserServiceImpl.class));
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
        assertNotNull(applicationContext.getBean(UsernameAndPasswordGenerator.class));
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