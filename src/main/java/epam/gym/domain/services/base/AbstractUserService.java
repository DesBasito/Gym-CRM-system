package epam.gym.domain.services.base;

import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.domain.models.UserModel;
import epam.gym.infrastructure.entities.UserHolder;
import epam.gym.infrastructure.mappers.BaseMapper;
import epam.gym.infrastructure.monitoring.metrics.UserMetrics;
import epam.gym.infrastructure.repositories.BaseUserRepository;
import epam.gym.util.UsernameAndPasswordGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
public abstract class AbstractUserService<T extends UserHolder,
        M extends UserModel, R extends BaseUserRepository<T>, Q, D> {

    protected final R repository;
    protected final BaseMapper<T, M, Q, D> mapper;

    protected UserMetrics userMetrics;

    protected AbstractUserService(R repository, BaseMapper<T, M, Q, D> mapper, UserMetrics userMetrics) {
        this.repository = repository;
        this.mapper = mapper;
        this.userMetrics = userMetrics;
    }

    protected abstract void updateEntityFields(T entity, Q request);
    protected abstract String getFullName(Q request);
    protected abstract void updateEntityFieldsFromUpdateRequest(T entity, Object updateRequest);
    protected abstract void setIsActiveFromUpdateRequest(T entity, Object updateRequest);

    protected void beforeCreate(T entity, Q request) {}

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public RegistrationResponse create(Q request) {
        log.info("Creating user: {}", getFullName(request));

        M model = mapper.requestToModel(request);
        setGeneratedCredentials(model);

        T entity = mapper.toEntity(model);
        beforeCreate(entity, request);

        T created = repository.save(entity);

        if (userMetrics != null) {
            userMetrics.incrementUserRegistration();
        }

        RegistrationResponse response = RegistrationResponse.builder()
                .username(created.getUser().getUsername())
                .password(created.getUser().getPassword())
                .build();

        log.info("User created successfully with username: {}", response.getUsername());
        return response;
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
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

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public void delete(String username) {
        log.info("Deleting user by username: {}", username);

        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        repository.delete(entity.getUser().getId());
        log.info("User deleted successfully with username: {}", username);
    }

    @Transactional(noRollbackFor = NoSuchElementException.class)
    public M select(Long id) {
        log.info("Selecting user with id: {}", id);
        T entity = repository.findById(id);
        if (entity == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return mapper.toModel(entity);
    }

    @Transactional(readOnly = true)
    public D selectByUsername(String username) {
        log.info("Selecting user profile by username: {}", username);

        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        D dto = mapper.toDto(entity);
        log.info("User profile found for username: {}", username);
        return dto;
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for user: {}", username);

        if (!repository.authenticate(username, oldPassword)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        repository.changePassword(entity.getUser().getId(), newPassword);
        log.info("Password changed successfully for user: {}", username);
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public void setActiveStatus(String username, Boolean isActive) {
        log.info("Setting active status for user: {} to {}", username, isActive);

        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        if (Boolean.TRUE.equals(isActive)) {
            repository.activate(entity.getUser().getId());
            log.info("User activated with username: {}", username);
        } else {
            repository.deactivate(entity.getUser().getId());
            log.info("User deactivated with username: {}", username);
        }
    }

    @Transactional(rollbackFor = {IllegalArgumentException.class, NoSuchElementException.class})
    public D updateByUsername(Object updateRequest, String username) {
        log.info("Updating user profile by username: {}", username);

        T entity = repository.findByUsername(username);
        if (entity == null) {
            throw new NoSuchElementException("User not found with username: " + username);
        }

        updateEntityFieldsFromUpdateRequest(entity, updateRequest);
        setIsActiveFromUpdateRequest(entity, updateRequest);

        repository.save(entity);
        D dto = mapper.toDto(entity);

        log.info("User profile updated successfully for username: {}", username);
        return dto;
    }

    protected void setGeneratedCredentials(M model) {
        String baseUsername = UsernameAndPasswordGenerator.generateAndGetUsername(model.getFirstName(), model.getLastName());
        String username = baseUsername;
        int suffix = 1;

        while (repository.findByUsername(username) != null) {
            username = baseUsername + suffix;
            suffix++;
        }

        model.setUsername(username);
        model.setPassword(UsernameAndPasswordGenerator.generateAndGetPassword());
    }
}
