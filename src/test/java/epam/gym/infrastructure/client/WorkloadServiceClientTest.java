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
@DisplayName("WorkloadServiceClient RabbitMQ Tests")
class WorkloadServiceClientTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private WorkloadServiceClient workloadServiceClient;

    @Test
    @DisplayName("Should send workload message to RabbitMQ successfully")
    void testSendWorkloadUpdate_success() {
        WorkloadRequest request = buildRequest();

        workloadServiceClient.sendWorkloadUpdate(request);

        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.WORKLOAD_EXCHANGE),
                eq(RabbitMQConfig.WORKLOAD_ROUTING_KEY),
                eq(request),
                any(org.springframework.amqp.core.MessagePostProcessor.class)
        );
    }

    @Test
    @DisplayName("Should handle RabbitMQ exception gracefully")
    void testSendWorkloadUpdate_rabbitMQFailure() {
        WorkloadRequest request = buildRequest();

        doThrow(new AmqpException("Connection refused"))
                .when(rabbitTemplate)
                .convertAndSend(anyString(), anyString(), any(WorkloadRequest.class),
                        any(org.springframework.amqp.core.MessagePostProcessor.class));

        assertDoesNotThrow(() -> workloadServiceClient.sendWorkloadUpdate(request));
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