package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TrainingRequest;
import epam.gym.domain.dto.response.TrainingDto;
import epam.gym.domain.models.TrainingModel;
import epam.gym.infrastructure.entities.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainee", ignore = true)
    @Mapping(target = "trainer", ignore = true)
    @Mapping(target = "trainingType", ignore = true)
    Training requestToEntity(TrainingRequest request);

    @Mapping(source = "trainingType.id", target = "trainingTypeId")
    @Mapping(source = "trainee.id", target = "traineeId")
    @Mapping(source = "trainer.id", target = "trainerId")
    TrainingModel entityToModel(Training training);

    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    @Mapping(source = "trainer.user.firstName", target = "trainerName", defaultExpression = "java(training.getTrainer().getUser().getFirstName() + \" \" + training.getTrainer().getUser().getLastName())")
    TrainingDto entityToDto(Training training);
}