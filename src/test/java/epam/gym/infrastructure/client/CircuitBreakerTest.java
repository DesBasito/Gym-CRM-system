package epam.gym.infrastructure.client;

import epam.gym.domain.dto.request.WorkloadRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkloadServiceImpl Tests")
class WorkloadServiceImplTest {

    @Mock
    private WorkloadServiceClient workloadServiceClient;

    @InjectMocks
    private WorkloadServiceImpl workloadService;

    @Test
    @DisplayName("Should delegate workload update to WorkloadServiceClient")
    void testUpdateWorkload_delegatesToClient() {
        WorkloadRequest request = buildRequest();

        workloadService.updateWorkload(request);

        verify(workloadServiceClient).sendWorkloadUpdate(request);
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