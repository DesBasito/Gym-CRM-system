package epam.gym.domain.services.interfaces;

import epam.gym.infrastructure.client.dto.WorkloadRequest;

public interface WorkloadService {

    void updateWorkload(WorkloadRequest request);
}