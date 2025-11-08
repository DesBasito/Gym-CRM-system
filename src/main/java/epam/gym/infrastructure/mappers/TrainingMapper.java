package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Training;
import epam.gym.infrastructure.dao.TrainingDao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingMapper {
    @Mapping(source = "trainingId", target = "trainingDaoId")
    TrainingDao toDao(Training training);
    @Mapping(source = "trainingDaoId", target = "trainingId")
    Training toModel(TrainingDao trainingDao);
}