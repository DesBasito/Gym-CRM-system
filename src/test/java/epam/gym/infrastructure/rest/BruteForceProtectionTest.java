package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.LoginRequest;
import epam.gym.infrastructure.security.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BruteForceProtectionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String TEST_USERNAME = "testuser";
    private static final String WRONG_PASSWORD = "wrongpassword";

    @BeforeEach
    void setUp() {
        loginAttemptService.loginSucceeded(TEST_USERNAME);
    }

    @Test
    void testBruteForceProtection_shouldBlockAfterThreeFailedAttempts() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, WRONG_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("locked")));
    }

    @Test
    void testBruteForceProtection_filterShouldBlockBeforeAuthentication() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, WRONG_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isLocked())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("minute")));
    }

    @Test
    void testBruteForceProtection_shouldShowRemainingTime() throws Exception {
        LoginRequest loginRequest = new LoginRequest(TEST_USERNAME, WRONG_PASSWORD);
        String requestBody = objectMapper.writeValueAsString(loginRequest);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(LOGIN_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.matchesRegex(
                        ".*[0-9]+ minute\\(s\\) & [0-9]+ second\\(s\\).*")));
    }
}