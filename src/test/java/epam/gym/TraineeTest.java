package epam.gym;

import epam.gym.config.ApplicationConfig;
import epam.gym.config.StorageConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Unit test for simple App.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ApplicationConfig.class, StorageConfig.class})
public class TraineeTest {

}
