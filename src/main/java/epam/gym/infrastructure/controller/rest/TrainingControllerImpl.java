package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.TraineeTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainerTrainingsFilterRequest;
import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.domain.services.interfaces.TrainingService;
import epam.gym.infrastructure.controller.interfaces.TrainingController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<List<TrainingDto>> getTraineeTrainings(TraineeTrainingsFilterRequest filterRequest) {
        log.info("Get trainee trainings request received for username: {}, filters: from={}, to={}, trainer={}, type={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTrainerName(), filterRequest.getTrainingType());

        List<TrainingDto> trainings = trainingService.selectTraineeTrainings(filterRequest);

        log.info("Trainee trainings retrieved successfully for username: {}, count: {}",
                filterRequest.getUsername(), trainings.size());
        return ResponseEntity.ok(trainings);
    }

    @Override
    public ResponseEntity<List<TrainingDto>> getTrainerTrainings(TrainerTrainingsFilterRequest filterRequest) {
        log.info("Get trainer trainings request received for username: {}, filters: from={}, to={}, trainee={}",
                filterRequest.getUsername(), filterRequest.getPeriodFrom(), filterRequest.getPeriodTo(),
                filterRequest.getTraineeName());

        List<TrainingDto> trainings = trainingService.selectTrainerTrainings(filterRequest);

        log.info("Trainer trainings retrieved successfully for username: {}, count: {}",
                filterRequest.getUsername(), trainings.size());
        return ResponseEntity.ok(trainings);
    }
}
