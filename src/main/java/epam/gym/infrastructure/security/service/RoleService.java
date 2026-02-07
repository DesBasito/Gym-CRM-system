package epam.gym.infrastructure.security.service;

import epam.gym.infrastructure.entities.Role;
import epam.gym.constants.RoleName;
import epam.gym.infrastructure.entities.User;
import epam.gym.infrastructure.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @Transactional
    public void assignRoleToUser(User user, RoleName roleName) {
        log.debug("Assigning role {} to user {}", roleName, user.getUsername());

        Role role = roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        user.getRoles().add(role);
        log.debug("Role {} assigned to user {}", roleName, user.getUsername());
    }
}