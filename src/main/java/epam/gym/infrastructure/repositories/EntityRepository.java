package epam.gym.infrastructure.repositories;

public interface EntityRepository<T, ID> {
    T save(T entity);
    T select(ID id);

    default void delete(ID id) {
        throw new UnsupportedOperationException("Delete operation is not supported");
    }
}
