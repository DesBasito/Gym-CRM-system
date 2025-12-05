package epam.gym.infrastructure.mappers;

import epam.gym.domain.dto.response.TrainingTypeDto;
import epam.gym.infrastructure.entities.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingTypeMapper {
    @Mapping(target = "trainingTypeName", expression = """
            java(
            trainingType.getTrainingTypeName() != null\s
            ? trainingType.getTrainingTypeName().name()\s
            : null)""")
    TrainingTypeDto toDto(TrainingType trainingType);
}
