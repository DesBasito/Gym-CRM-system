package epam.gym.infrastructure.mappers;

import epam.gym.domain.entities.Trainer;
import epam.gym.infrastructure.dao.TrainerDao;
import epam.gym.infrastructure.dao.UserDao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {

    TrainerDao toDao(Trainer trainee);
    Trainer toModel(TrainerDao traineeDao);
}
