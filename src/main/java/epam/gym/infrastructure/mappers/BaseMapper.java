package epam.gym.infrastructure.mappers;

import epam.gym.domain.models.UserModel;
import epam.gym.infrastructure.entities.UserHolder;

public interface BaseMapper<T extends UserHolder, M extends UserModel, Q, D> {
    M requestToModel(Q request);
    T toEntity(M model);
    M toModel(T entity);
    D toDto(T entity);
}
