package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.TraineeProfileDto;
import epam.gym.domain.models.TraineeModel;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.entities.TrainingType;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper extends BaseMapper<Trainee, TraineeModel, TraineeRequest, TraineeProfileDto> {

    @Mapping(target = "isActive", constant = "true")
    TraineeModel requestToModel(TraineeRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "dateOfBirth", source = "dateOfBirth")
    @Mapping(target = "address", source = "address")
    Trainee toEntity(TraineeModel model);


    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "dateOfBirth", target = "dateOfBirth")
    @Mapping(source = "address", target = "address")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainers", target = "trainers")
    TraineeProfileDto toDto(Trainee trainee);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.id", target = "userId")
    TraineeModel toModel(Trainee trainee);

    default String map(TrainingType trainingType) {
        return trainingType != null && trainingType.getTrainingTypeName() != null
                ? trainingType.getTrainingTypeName().name()
                : null;
    }
}
