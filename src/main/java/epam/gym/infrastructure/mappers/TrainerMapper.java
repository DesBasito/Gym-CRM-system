package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.request.TrainerRequest;
import epam.gym.domain.dto.response.TrainerInfoDto;
import epam.gym.domain.dto.response.TrainerProfileDto;
import epam.gym.domain.models.TrainerModel;
import epam.gym.infrastructure.entities.Trainer;
import epam.gym.infrastructure.entities.TrainingType;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper extends BaseMapper<Trainer, TrainerModel, TrainerRequest, TrainerProfileDto>{
    @Mapping(target = "isActive", constant = "true")
    TrainerModel requestToModel(TrainerRequest request);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "user.firstName", source = "firstName")
    @Mapping(target = "user.lastName", source = "lastName")
    @Mapping(target = "user.isActive", source = "isActive")
    @Mapping(target = "user.username", source = "username")
    @Mapping(target = "user.password", source = "password")
    @Mapping(target = "trainees", ignore = true)
    @Mapping(target = "trainings", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    Trainer toEntity(TrainerModel model);


    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "id", target = "id")
    @Mapping(source = "trainees", target = "trainees")
    @Mapping(target = "specialization", expression = """
            java(
            trainer.getSpecialization() != null\s
            && trainer.getSpecialization().getTrainingTypeName() != null\s
            ? trainer.getSpecialization().getTrainingTypeName().name()\s
            : null)""")
    TrainerProfileDto toDto(Trainer trainer);

    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.isActive", target = "isActive")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "specialization", expression = """
            java(
            trainer.getSpecialization() != null\s
            && trainer.getSpecialization().getTrainingTypeName() != null\s
            ? trainer.getSpecialization().getTrainingTypeName().name()\s
            : null)""")
    TrainerModel toModel(Trainer trainer);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "username", target = "username")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "specialization", target = "specialization")
    TrainerInfoDto modelToInfoDto(TrainerModel model);

    default String map(TrainingType trainingType) {
        return trainingType != null && trainingType.getTrainingTypeName() != null
                ? trainingType.getTrainingTypeName().name()
                : null;
    }
}