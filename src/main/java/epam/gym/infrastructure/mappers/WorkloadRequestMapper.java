package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.WorkloadRequest;
import epam.gym.infrastructure.entities.Training;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkloadRequestMapper {

    public static WorkloadRequest fromTraining(Training training, WorkloadRequest.ActionType actionType) {
        return WorkloadRequest.builder()
                .username(training.getTrainer().getUser().getUsername())
                .firstName(training.getTrainer().getUser().getFirstName())
                .lastName(training.getTrainer().getUser().getLastName())
                .isActive(training.getTrainer().getUser().getIsActive())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .actionType(actionType)
                .build();
    }
}