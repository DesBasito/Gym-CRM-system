package epam.gym.cucumber.component.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import epam.gym.domain.dto.request.TrainingRequest;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TrainingStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SharedTestContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @When("I create a training with trainee {string} trainer {string} name {string} type {string} date {string} duration {int}")
    public void iCreateTraining(String traineeUsername, String trainerUsername, String name,
                                String type, String date, int duration) throws Exception {
        TrainingRequest request = new TrainingRequest();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsername(trainerUsername);
        request.setTrainingName(name);
        request.setTrainingType(type);
        request.setTrainingDate(LocalDate.parse(date));
        request.setTrainingDuration(duration);

        MockHttpServletRequestBuilder builder = post("/api/v1/trainings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request));
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("I GET trainee trainings for username {string}")
    public void iGetTraineeTrainings(String username) throws Exception {
        MockHttpServletRequestBuilder builder = get("/api/v1/trainings/trainee")
                .param("username", username);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("I GET trainer trainings for username {string}")
    public void iGetTrainerTrainings(String username) throws Exception {
        MockHttpServletRequestBuilder builder = get("/api/v1/trainings/trainer")
                .param("username", username);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }
}
