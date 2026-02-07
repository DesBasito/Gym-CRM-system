package epam.gym.infrastructure.actuator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ActuatorEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testHealthEndpoint_shouldReturnUpWithAllComponents() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components").exists())
                .andExpect(jsonPath("$.components.database").exists())
                .andExpect(jsonPath("$.components.database.status").exists())
                .andExpect(jsonPath("$.components.database.details.database").value("PostgreSQL"))
                .andExpect(jsonPath("$.components.diskSpace").exists())
                .andExpect(jsonPath("$.components.diskSpace.details.total").exists())
                .andExpect(jsonPath("$.components.memory").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testPrometheusEndpoint_shouldReturnCustomMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/plain"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# HELP")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("# TYPE")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("gym_trainings_total")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("gym_trainings_active")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("gym_users_registered_total")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("gym_users_active")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("gym_requests_total")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("application=\"gym-crm-system\"")));
    }
}