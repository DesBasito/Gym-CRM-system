package epam.gym.infrastructure.repositories;

import epam.gym.infrastructure.entities.UserHolder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public abstract class BaseUserRepository<T extends UserHolder> {

    protected EntityManager entityManager;
    protected Class<T> entityClass;

    @Autowired
    BaseUserRepository(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        if (entity.getId() == null) {
            entityManager.persist(entity);
            log.info("{} created with username: {}", getEntityName(), entity.getUser().getUsername());
        } else {
            entity = entityManager.merge(entity);
            log.info("{} updated with username: {}", getEntityName(), entity.getUser().getUsername());
        }
        return entity;
    }

    public T findById(Long id) {
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            log.warn("{} not found with id: {}", getEntityName(), id);
        }
        return entity;
    }

    @SuppressWarnings("unchecked")
    public T findByUsername(String username) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT t FROM " + entityClass.getSimpleName() + " t WHERE t.user.username = :username",
                    entityClass);
            query.setParameter("username", username);
            return (T) query.getSingleResult();
        } catch (NoResultException e) {
            log.warn("{} not found with username: {}", getEntityName(), username);
            return null;
        }
    }

    public void delete(Long id) {
        T entity = findById(id);
        if (entity == null) {
            log.warn("{} not found for deletion with id: {}", getEntityName(), id);
            throw new IllegalArgumentException(getEntityName() + " not found with id: " + id);
        }
        entity.removeAssociations();
        entityManager.remove(entity);
        entityManager.flush();
        log.info("{} deleted with id: {}", getEntityName(), id);
    }

    public boolean authenticate(String username, String password) {
        try {
            Query query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM " + entityClass.getSimpleName() +
                    " t WHERE t.user.username = :username AND t.user.password = :password",
                    Long.class);
            query.setParameter("username", username);
            query.setParameter("password", password);
            return (Long) query.getSingleResult() > 0;
        } catch (IllegalArgumentException e) {
            log.error("Error during {} authentication: {}", getEntityName(), e.getMessage());
            return false;
        }
    }

    public void changePassword(Long id, String newPassword) {
        T entity = findById(id);
        if (entity == null) {
            log.warn("{} not found for password change with id: {}", getEntityName(), id);
            throw new IllegalArgumentException(getEntityName() + " not found with id: " + id);
        }

        entity.getUser().setPassword(newPassword);
        entityManager.merge(entity);
        log.info("Password changed for {} with id: {}", getEntityName(), id);
    }

    public void changePassword(String username, String newPassword) {
        T entity = findByUsername(username);
        if (entity == null) {
            log.warn("{} not found for password change: {}", getEntityName(), username);
            throw new IllegalArgumentException(getEntityName() + " not found with username: " + username);
        }

        entity.getUser().setPassword(newPassword);
        entityManager.merge(entity);
        log.info("Password changed for {}: {}", getEntityName(), username);
    }

    public void activate(Long id) {
        T entity = findById(id);
        if (entity == null) {
            log.warn("{} not found for activation with id: {}", getEntityName(), id);
            throw new IllegalArgumentException(getEntityName() + " not found with id: " + id);
        }

        entity.getUser().setIsActive(true);
        entityManager.merge(entity);
        log.info("{} activated with id: {}", getEntityName(), id);
    }

    public void activate(String username) {
        T entity = findByUsername(username);
        if (entity == null) {
            log.warn("{} not found for activation: {}", getEntityName(), username);
            throw new IllegalArgumentException(getEntityName() + " not found with username: " + username);
        }

        entity.getUser().setIsActive(true);
        entityManager.merge(entity);
        log.info("{} activated: {}", getEntityName(), username);
    }

    public void deactivate(Long id) {
        T entity = findById(id);
        if (entity == null) {
            log.warn("{} not found for deactivation with id: {}", getEntityName(), id);
            throw new IllegalArgumentException(getEntityName() + " not found with id: " + id);
        }

        entity.getUser().setIsActive(false);
        entityManager.merge(entity);
        log.info("{} deactivated with id: {}", getEntityName(), id);
    }

    public void deactivate(String username) {
        T entity = findByUsername(username);
        if (entity == null) {
            log.warn("{} not found for deactivation: {}", getEntityName(), username);
            throw new IllegalArgumentException(getEntityName() + " not found with username: " + username);
        }

        entity.getUser().setIsActive(false);
        entityManager.merge(entity);
        log.info("{} deactivated: {}", getEntityName(), username);
    }

    public List<T> findAll(int offset, int limit) {
        Query query = entityManager.createQuery(
                "SELECT t FROM " + entityClass.getSimpleName() + " t",
                entityClass);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public long count() {
        Query query = entityManager.createQuery(
                "SELECT COUNT(t) FROM " + entityClass.getSimpleName() + " t",
                Long.class);
        return (Long) query.getSingleResult();
    }

    protected String getEntityName() {
        return entityClass.getSimpleName();
    }
}