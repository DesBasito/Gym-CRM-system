package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.models.TraineeModel;
import epam.gym.infrastructure.entities.Trainee;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper extends BaseMapper<Trainee, TraineeModel, TraineeRequest> {

    TraineeModel requestToModel(TraineeRequest request);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "trainers", ignore = true)
    Trainee toEntity(TraineeModel model);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.id", target = "userId")
    TraineeModel toModel(Trainee trainee);
}
