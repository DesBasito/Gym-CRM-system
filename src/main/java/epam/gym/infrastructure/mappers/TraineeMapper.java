package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Trainee;
import epam.gym.infrastructure.dao.TraineeDao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;


@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper {

    TraineeDao toDao(Trainee trainee);
    Trainee toModel(TraineeDao traineeDao);
}
