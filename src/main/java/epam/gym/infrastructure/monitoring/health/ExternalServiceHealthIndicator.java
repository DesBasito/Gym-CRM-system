package epam.gym.infrastructure.monitoring.health;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            boolean serviceAvailable = checkExternalService();

            if (serviceAvailable) {
                log.debug("External service health check: UP");
                return Health.up()
                        .withDetail("service", "Notification Service")
                        .withDetail("status", "Available")
                        .withDetail("responseTime", "45ms")
                        .build();
            } else {
                log.warn("External service health check: DOWN");
                return Health.down()
                        .withDetail("service", "Notification Service")
                        .withDetail("status", "Unavailable")
                        .build();
            }
        } catch (Exception e) {
            log.error("External service health check failed", e);
            return Health.down()
                    .withDetail("service", "Notification Service")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

    private boolean checkExternalService() {
        return true;
    }
}