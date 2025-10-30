package epam.gym.dao;

import java.util.List;

public interface EntityDao<T, ID> {
    T create(T entity);
    List<T> select();

    default T update(T entity) {
        throw new UnsupportedOperationException("Update operation is not supported");
    }

    default void delete(ID id) {
        throw new UnsupportedOperationException("Delete operation is not supported");
    }
}
