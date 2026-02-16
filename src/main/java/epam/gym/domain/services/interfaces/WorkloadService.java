package epam.gym.domain.services.interfaces;

import epam.gym.domain.dto.request.WorkloadRequest;

public interface WorkloadService {

    void updateWorkload(WorkloadRequest request);
}