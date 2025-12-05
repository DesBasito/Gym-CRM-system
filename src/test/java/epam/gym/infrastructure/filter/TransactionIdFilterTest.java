package epam.gym.infrastructure.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionIdFilterTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    void testTransactionIdIsGeneratedWhenNotProvided() throws Exception {
        when(request.getHeader("X-Transaction-Id")).thenReturn(null);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");

        TransactionIdFilter filter = new TransactionIdFilter();
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("transactionId")).isNull();
    }

    @Test
    void testTransactionIdFromHeaderIsUsed() throws Exception {
        String customTransactionId = "custom-transaction-123";
        when(request.getHeader("X-Transaction-Id")).thenReturn(customTransactionId);
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/api/v1/test");

        TransactionIdFilter filter = new TransactionIdFilter();
        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(MDC.get("transactionId")).isNull();
    }
}