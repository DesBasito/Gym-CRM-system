package epam.gym.infrastructure.client;

import epam.gym.domain.services.interfaces.WorkloadService;
import epam.gym.infrastructure.client.dto.WorkloadRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final WorkloadServiceClient workloadServiceClient;

    @Override
    public void updateWorkload(WorkloadRequest request) {
        workloadServiceClient.updateWorkload(request);
    }
}