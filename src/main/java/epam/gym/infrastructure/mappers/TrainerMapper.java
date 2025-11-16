package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.models.TrainerModel;
import epam.gym.infrastructure.entities.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper extends BaseMapper<Trainer, TrainerModel, TrainerRequest>{
    TrainerModel requestToModel(TrainerRequest request);

    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    Trainer toEntity(TrainerModel model);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "specialization.trainingTypeName", target = "specialization")
    TrainerModel toModel(Trainer trainer);
}