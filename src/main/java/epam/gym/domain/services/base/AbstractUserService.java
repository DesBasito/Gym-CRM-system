package epam.gym.domain.services.base;

import epam.gym.domain.models.UserModel;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.mappers.BaseMapper;
import epam.gym.infrastructure.repositories.BaseUserRepository;
import epam.gym.util.UsernameAndPasswordGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;

@Slf4j
public abstract class AbstractUserService<T extends User,
        M extends UserModel, R extends BaseUserRepository<T>, Q> {

    protected final R repository;
    protected final BaseMapper<T, M, Q> mapper;

    protected AbstractUserService(R repository, BaseMapper<T, M, Q> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    protected abstract void updateEntityFields(T entity, Q request);
    protected abstract String getFullName(Q request);

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public M create(Q request) {
        log.info("Creating user: {}", getFullName(request));

        M model = mapper.requestToModel(request);
        setGeneratedCredentials(model);

        T entity = mapper.toEntity(model);
        T created = repository.save(entity);
        M result = mapper.toModel(created);

        log.info("User created successfully with username: {}", result.getUsername());
        return result;
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public M update(Q request, String username) {
        log.info("Updating user: {}", getFullName(request));

        T current = repository.findByUsername(username);
        if (current == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        updateEntityFields(current, request);
        T updated = repository.save(current);
        M result = mapper.toModel(updated);

        log.info("User updated successfully with username: {}", result.getUsername());
        return result;
    }

    @Transactional(dontRollbackOn = NoSuchElementException.class)
    public M select(String username) {
        log.info("Selecting user: {}", username);
        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }
        return mapper.toModel(entity);
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void activate(String username) {
        log.info("Activating user: {}", username);
        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        if (Boolean.FALSE.equals(entity.getIsActive())) {
            repository.activate(username);
            log.info("User activated successfully: {}", username);
        } else {
            log.info("User {} is already active, skipping activation", username);
        }
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void deactivate(String username) {
        log.info("Deactivating user: {}", username);
        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        if (Boolean.TRUE.equals(entity.getIsActive())) {
            repository.deactivate(username);
            log.info("User deactivated successfully: {}", username);
        } else {
            log.info("User {} is already inactive, skipping deactivation", username);
        }
    }

    @Transactional(rollbackOn = {IllegalArgumentException.class, NoSuchElementException.class})
    public void changePassword(String username, String newPassword) {
        log.info("Changing password for user: {}", username);
        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }
        repository.changePassword(username, newPassword);
        log.info("Password changed successfully for user: {}", username);
    }

    protected void setGeneratedCredentials(M model) {
        model.setUsername(UsernameAndPasswordGenerator.generateAndGetUsername(model.getFirstName(), model.getLastName()));
        model.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
    }
}
