package epam.gym.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.config.TestConfig;
import epam.gym.domain.dto.request.TraineeRequest;
import epam.gym.domain.dto.response.RegistrationResponse;
import epam.gym.infrastructure.controller.rest.TraineeControllerImpl;
import epam.gym.infrastructure.entities.Trainee;
import epam.gym.infrastructure.repositories.TraineeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@Transactional
class TraineeControllerTest {

    @Autowired
    private TraineeControllerImpl traineeControllerImpl;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private EntityManager entityManager;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(traineeControllerImpl).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainees").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        entityManager.flush();
    }

    @Test
    void testRegisterTrainee_shouldCreateTraineeAndReturnCredentials() throws Exception {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));
        request.setAddress("123 Main St");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").exists())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertNotNull(response.getUsername());
        assertNotNull(response.getPassword());
        assertEquals("John.Doe", response.getUsername());
        assertEquals(10, response.getPassword().length());

        Trainee trainee = traineeRepository.findByUsername(response.getUsername());
        assertNotNull(trainee);
        assertEquals("John", trainee.getUser().getFirstName());
        assertEquals("Doe", trainee.getUser().getLastName());
        assertTrue(trainee.getUser().getIsActive());
        assertEquals(LocalDate.of(1990, 1, 1), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void testRegisterTrainee_withDuplicateName_shouldGenerateUniqueUsername() throws Exception {
        TraineeRequest request1 = new TraineeRequest();
        request1.setFirstName("John");
        request1.setLastName("Doe");

        mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe"));

        TraineeRequest request2 = new TraineeRequest();
        request2.setFirstName("John");
        request2.setLastName("Doe");

        MvcResult result = mockMvc.perform(post("/api/v1/trainees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("John.Doe1"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseBody, RegistrationResponse.class);

        assertEquals("John.Doe1", response.getUsername());
    }
}
