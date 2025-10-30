package epam.gym.facade;

import epam.gym.services.interfaces.TraineeService;
import epam.gym.services.interfaces.TrainerService;
import epam.gym.services.interfaces.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymFacade {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;


}
