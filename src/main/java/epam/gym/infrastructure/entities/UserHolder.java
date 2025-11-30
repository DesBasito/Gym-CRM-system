package epam.gym.infrastructure.entities;

public interface UserHolder {
    Long getId();
    User getUser();
    void removeAssociations();
}
