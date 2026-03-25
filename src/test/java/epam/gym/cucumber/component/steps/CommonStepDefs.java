package epam.gym.cucumber.component.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CommonStepDefs {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SharedTestContext context;

    @PersistenceContext
    private EntityManager entityManager;

    @Before
    @Transactional
    public void cleanUpDatabase() {
        entityManager.createNativeQuery("DELETE FROM trainings").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM trainers_trainees").executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM trainees WHERE user_id NOT IN " +
                "(SELECT id FROM users WHERE username IN ('Alice.Brown', 'Bob.Wilson', 'Charlie.Davis'))"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM trainers WHERE user_id NOT IN " +
                "(SELECT id FROM users WHERE username IN ('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Sarah.Connor'))"
        ).executeUpdate();
        entityManager.createNativeQuery(
                "DELETE FROM users WHERE username NOT IN " +
                "('John.Doe', 'Jane.Smith', 'Mike.Johnson', 'Alice.Brown', 'Bob.Wilson', 'Charlie.Davis', 'Sarah.Connor')"
        ).executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE users ALTER COLUMN id RESTART WITH 100").executeUpdate();
        entityManager.createNativeQuery("ALTER TABLE trainings ALTER COLUMN id RESTART WITH 100").executeUpdate();
        entityManager.flush();
        context.clearAuthentication();
    }

    @Given("I am authenticated as {string} with role {string}")
    public void iAmAuthenticatedAsWithRole(String username, String role) {
        context.setAuthentication(username, role);
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(int expectedStatus) throws Exception {
        assertThat(context.getLastStatus()).isEqualTo(expectedStatus);
    }

    @Then("the response is a JSON array")
    public void theResponseIsAJsonArray() throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body.trim()).startsWith("[");
    }

    @Then("the response contains field {string} with value {string}")
    public void theResponseContainsFieldWithValue(String field, String value) throws Exception {
        String body = context.getLastResponseBody();
        assertThat(body).contains("\"" + field + "\"").contains(value);
    }

    @When("I GET {string}")
    public void iGet(String path) throws Exception {
        MockHttpServletRequestBuilder builder = get(path);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }

    @When("I DELETE {string}")
    public void iDelete(String path) throws Exception {
        MockHttpServletRequestBuilder builder = delete(path);
        if (context.isAuthenticated()) {
            builder = (MockHttpServletRequestBuilder) builder.with(context.getSecurityProcessor());
        }
        MvcResult result = mockMvc.perform(builder).andReturn();
        context.setLastResult(result);
    }
}
