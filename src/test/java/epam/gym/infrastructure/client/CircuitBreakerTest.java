package epam.gym.infrastructure.client;

import epam.gym.domain.dto.request.WorkloadRequest;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("WorkloadService Circuit Breaker Tests")
class CircuitBreakerTest {

    @Autowired
    private WorkloadServiceImpl workloadService;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockitoBean
    private WorkloadServiceClient workloadServiceClient;

    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        circuitBreaker = circuitBreakerRegistry.circuitBreaker("workloadService");
        circuitBreaker.reset();
    }

    @Test
    @DisplayName("Should have circuit breaker configured")
    void testCircuitBreakerExists() {
        assertNotNull(circuitBreaker);
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    @DisplayName("Should successfully call workload service when circuit is closed")
    void testSuccessfulCall_circuitClosed() {
        WorkloadRequest request = buildRequest();

        doNothing().when(workloadServiceClient).updateWorkload(any(WorkloadRequest.class));

        assertDoesNotThrow(() -> workloadService.updateWorkload(request));

        verify(workloadServiceClient, times(1)).updateWorkload(request);
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    @DisplayName("Should invoke fallback when workload service fails")
    void testFailedCall_fallbackInvoked() {
        WorkloadRequest request = buildRequest();

        doThrow(FeignException.ServiceUnavailable.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        assertDoesNotThrow(() -> workloadService.updateWorkload(request));
    }

    @Test
    @DisplayName("Should open circuit after failure threshold is reached")
    void testCircuitOpensAfterFailures() {
        WorkloadRequest request = buildRequest();

        doThrow(FeignException.ServiceUnavailable.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        for (int i = 0; i < 10; i++) {
            workloadService.updateWorkload(request);
        }

        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    @DisplayName("Should record metrics for successful and failed calls")
    void testCircuitBreakerMetrics() {
        WorkloadRequest request = buildRequest();

        doNothing().when(workloadServiceClient).updateWorkload(any(WorkloadRequest.class));
        workloadService.updateWorkload(request);
        workloadService.updateWorkload(request);

        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertEquals(2, metrics.getNumberOfSuccessfulCalls());
        assertEquals(0, metrics.getNumberOfFailedCalls());
    }

    private WorkloadRequest buildRequest() {
        return WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(null)
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();
    }
}