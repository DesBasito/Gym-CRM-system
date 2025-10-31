package epam.gym.dao;

import java.util.List;
import java.util.Optional;

public interface EntityDao<T, ID> {
    Optional<T> create(T entity);
    Optional<T> select(ID id);

    default Optional<T> update(T entity) {
        throw new UnsupportedOperationException("Update operation is not supported");
    }

    default boolean delete(ID id) {
        throw new UnsupportedOperationException("Delete operation is not supported");
    }
}
