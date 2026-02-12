package epam.gym.infrastructure.client;

import epam.gym.infrastructure.client.dto.WorkloadRequest;
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
        WorkloadRequest request = WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(null)
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        doNothing().when(workloadServiceClient).updateWorkload(any(WorkloadRequest.class));

        assertDoesNotThrow(() -> workloadService.updateWorkload(request));

        verify(workloadServiceClient, times(1)).updateWorkload(request);
        assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.getState());
    }

    @Test
    @DisplayName("Should propagate exception when workload service fails")
    void testFailedCall_exceptionPropagated() {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(null)
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        doThrow(FeignException.ServiceUnavailable.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        assertThrows(FeignException.ServiceUnavailable.class,
                () -> workloadService.updateWorkload(request));

        verify(workloadServiceClient, times(1)).updateWorkload(request);
    }

    @Test
    @DisplayName("Should open circuit after failure threshold is reached")
    void testCircuitOpensAfterFailures() {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(null)
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        doThrow(FeignException.ServiceUnavailable.class)
                .when(workloadServiceClient)
                .updateWorkload(any(WorkloadRequest.class));

        // Make multiple failed calls to exceed the minimum-number-of-calls (5) and failure-rate-threshold (50%)
        for (int i = 0; i < 10; i++) {
            try {
                workloadService.updateWorkload(request);
            } catch (Exception e) {
                // Expected exception, continue
            }
        }

        // Circuit should be open after 10 failures (100% failure rate with window size 10)
        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());
    }

    @Test
    @DisplayName("Should record metrics for successful and failed calls")
    void testCircuitBreakerMetrics() {
        WorkloadRequest request = WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(null)
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();

        // Make some successful calls
        doNothing().when(workloadServiceClient).updateWorkload(any(WorkloadRequest.class));
        workloadService.updateWorkload(request);
        workloadService.updateWorkload(request);

        // Verify metrics are being recorded
        CircuitBreaker.Metrics metrics = circuitBreaker.getMetrics();
        assertEquals(2, metrics.getNumberOfSuccessfulCalls());
        assertEquals(0, metrics.getNumberOfFailedCalls());
    }
}
