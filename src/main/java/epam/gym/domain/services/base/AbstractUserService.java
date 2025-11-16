package epam.gym.domain.services.base;

import epam.gym.domain.models.UserModel;
import epam.gym.infrastructure.entities.UserHolder;
import epam.gym.infrastructure.mappers.BaseMapper;
import epam.gym.infrastructure.repositories.BaseUserRepository;
import epam.gym.util.UsernameAndPasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public abstract class AbstractUserService<T extends UserHolder,
        M extends UserModel, R extends BaseUserRepository<T>, Q> {

    protected final R repository;
    protected final BaseMapper<T, M, Q> mapper;

    protected AbstractUserService(R repository, BaseMapper<T, M, Q> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    protected abstract void updateEntityFields(T entity, Q request);
    protected abstract String getFullName(Q request);

    protected void beforeCreate(T entity, Q request) {}

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public M create(Q request) {
        log.info("Creating user: {}", getFullName(request));

        M model = mapper.requestToModel(request);
        setGeneratedCredentials(model);

        T entity = mapper.toEntity(model);
        beforeCreate(entity, request);

        T created = repository.save(entity);
        M result = mapper.toModel(created);

        log.info("User created successfully with username: {}", result.getUsername());
        return result;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public M update(Q request, Long id) {
        log.info("Updating user with id: {}", id);

        T current = repository.findById(id);
        if (current == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }

        updateEntityFields(current, request);
        T updated = repository.save(current);
        M result = mapper.toModel(updated);

        log.info("User updated successfully with id: {}", id);
        return result;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);

        T current = repository.findById(id);
        if (current == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        repository.delete(id);
        log.info("User deleted with id: {}", id);
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public M select(Long id) {
        log.info("Selecting user with id: {}", id);
        T entity = repository.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return mapper.toModel(entity);
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void activate(Long id) {
        log.info("Activating user with id: {}", id);
        T entity = repository.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }

        if (Boolean.FALSE.equals(entity.getUser().getIsActive())) {
            repository.activate(id);
            log.info("User activated successfully with id: {}", id);
        } else {
            log.info("User with id {} is already active, skipping activation", id);
        }
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void deactivate(Long id) {
        log.info("Deactivating user with id: {}", id);
        T entity = repository.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }

        if (Boolean.TRUE.equals(entity.getUser().getIsActive())) {
            repository.deactivate(id);
            log.info("User deactivated successfully with id: {}", id);
        } else {
            log.info("User with id {} is already inactive, skipping deactivation", id);
        }
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void changePassword(Long id, String newPassword) {
        log.info("Changing password for user with id: {}", id);
        T entity = repository.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        repository.changePassword(id, newPassword);
        log.info("Password changed successfully for user with id: {}", id);
    }

    protected void setGeneratedCredentials(M model) {
        model.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(model.getFirstName(), model.getLastName()));
        model.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
    }
}
