package epam.gym.infrastructure.client;

import epam.gym.config.RabbitMQConfig;
import epam.gym.domain.dto.request.WorkloadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadServiceImpl RabbitMQ Tests")
class WorkloadServiceImplTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private WorkloadServiceImpl workloadService;

    @Test
    @DisplayName("Should send workload message to RabbitMQ successfully")
    void testUpdateWorkload_success() {
        WorkloadRequest request = buildRequest();

        workloadService.updateWorkload(request);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WORKLOAD_EXCHANGE),
                eq(RabbitMQConfig.WORKLOAD_ROUTING_KEY),
                eq(request),
                any(org.springframework.amqp.core.MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ exception gracefully")
    void testUpdateWorkload_rabbitMQFailure() {
        WorkloadRequest request = buildRequest();

        doThrow(new AmqpException("Connection refused"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(WorkloadRequest.class),
                        any(org.springframework.amqp.core.MessagePostProcessor.class));

        assertDoesNotThrow(() -> workloadService.updateWorkload(request));
    }

    private WorkloadRequest buildRequest() {
        return WorkloadRequest.builder()
                .username("trainer.test")
                .firstName("Trainer")
                .lastName("Test")
                .isActive(true)
                .trainingDate(LocalDate.of(2026, 2, 15))
                .trainingDuration(60)
                .actionType(WorkloadRequest.ActionType.ADD)
                .build();
    }
}