package epam.gym.infrastructure.repositories;

import java.util.Optional;

public interface EntityRepository<T, ID> {
    Optional<T> save(T entity);
    Optional<T> select(ID id);

    default boolean delete(ID id) {
        throw new UnsupportedOperationException("Delete operation is not supported");
    }
}
