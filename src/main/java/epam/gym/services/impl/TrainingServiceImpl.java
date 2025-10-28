package epam.gym.services.impl;

import epam.gym.entities.Trainee;
import epam.gym.services.interfaces.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    @Override
    public Trainee create() {
        return null;
    }

    @Override
    public Trainee select() {
        return null;
    }
}
