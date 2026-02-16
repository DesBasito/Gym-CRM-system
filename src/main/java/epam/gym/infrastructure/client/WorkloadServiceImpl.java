package epam.gym.infrastructure.client;

import epam.gym.config.RabbitMQConfig;
import epam.gym.domain.dto.request.WorkloadRequest;
import epam.gym.domain.services.interfaces.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void updateWorkload(WorkloadRequest request) {
        try {
            log.info("Sending workload message to RabbitMQ. Action: {}, Trainer: {}",
                    request.getActionType(), request.getUsername());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.WORKLOAD_EXCHANGE,
                    RabbitMQConfig.WORKLOAD_ROUTING_KEY,
                    request
            );

            log.info("Workload message sent successfully for trainer: {}", request.getUsername());
        } catch (AmqpException ex) {
            log.error("Failed to send workload message to RabbitMQ. Action: {}, Trainer: {}. Error: {}",
                    request.getActionType(), request.getUsername(), ex.getMessage());
        }
    }
}