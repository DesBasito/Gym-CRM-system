package epam.gym.infrastructure.client;

import epam.gym.domain.dto.request.WorkloadRequest;
import epam.gym.domain.services.interfaces.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {

    private final WorkloadServiceClient workloadServiceClient;

    @Override
    public void updateWorkload(WorkloadRequest request) {
        workloadServiceClient.sendWorkloadUpdate(request);
    }
}