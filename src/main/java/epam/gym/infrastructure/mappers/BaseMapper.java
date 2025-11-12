package epam.gym.infrastructure.mappers;

import epam.gym.domain.models.UserModel;
import epam.gym.infrastructure.entities.User;

public interface BaseMapper<T extends User, M extends UserModel, Q> {
    M requestToModel(Q request);
    T toEntity(M model);
    M toModel(T entity);
}
