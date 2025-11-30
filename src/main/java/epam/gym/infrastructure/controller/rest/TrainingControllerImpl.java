package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.controller.interfaces.TrainingController;
import epam.gym.infrastructure.entities.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingControllerImpl implements TrainingController {
    private final TrainingService trainingService;

    @Override
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes() {
        log.info("Get training types request received for username");

        List<TrainingTypeDto> types = trainingService.getAllTrainingTypes();

        log.info("Training types retrieved successfully!");
        return ResponseEntity.ok(types);
    }

    @Override
    public ResponseEntity<Void> addTraining(TrainingRequest request) {
        log.info("Add training request received: trainee={}, trainer={}, name={}",
                request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingName());

        trainingService.create(request);

        log.info("Training created successfully: trainee={}, trainer={}, name={}",
                request.getTraineeUsername(), request.getTrainerUsername(), request.getTrainingName());
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(String username, LocalDate periodFrom,
                                                                  LocalDate periodTo, String trainerName, String trainingType) {
        log.info("Get trainee trainings request received for username: {}, filters: from={}, to={}, trainer={}, type={}",
                username, periodFrom, periodTo, trainerName, trainingType);

        List<TrainingDto> trainings = trainingService.selectTraineeTrainings(username, periodFrom, periodTo, trainingType);

        log.info("Trainee trainings retrieved successfully for username: {}, count: {}", username, trainings.size());
        return ResponseEntity.ok(trainings);
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(String username, LocalDate periodFrom,
                                                                  LocalDate periodTo, String traineeName) {
        log.info("Get trainer trainings request received for username: {}, filters: from={}, to={}, trainee={}",
                username, periodFrom, periodTo, traineeName);

        List<TrainingDto> trainings = trainingService.selectTrainerTrainings(username, periodFrom, periodTo);

        log.info("Trainer trainings retrieved successfully for username: {}, count: {}", username, trainings.size());
        return ResponseEntity.ok(trainings);
    }
}
