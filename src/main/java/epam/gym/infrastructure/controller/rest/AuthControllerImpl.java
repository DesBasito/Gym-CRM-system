package epam.gym.infrastructure.controller.rest;

import epam.gym.domain.dto.request.LoginRequest;
import epam.gym.domain.dto.response.AuthResponse;
import epam.gym.infrastructure.controller.interfaces.AuthController;
import epam.gym.security.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        log.info("Login request received for username: {}", request.getUsername());

        AuthResponse response = authService.login(request);

        log.info("Login successful for username: {}", request.getUsername());
        return ResponseEntity.ok(response);
    }
}