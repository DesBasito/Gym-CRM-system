package epam.gym.infrastructure.client;

import epam.gym.infrastructure.client.dto.WorkloadRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkloadServiceClientFallbackFactory implements FallbackFactory<WorkloadServiceClient> {

    @Override
    public WorkloadServiceClient create(Throwable cause) {
        return new WorkloadServiceClient() {
            @Override
            public void updateWorkload(WorkloadRequest request) {
                log.error("Failed to update workload for trainer: {}. Error: {}. Request will be skipped.",
                        request.getUsername(), cause.getMessage(), cause);
            }
        };
    }
}