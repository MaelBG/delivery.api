package com.deliverytech.delivery_api.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements Filter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID"; // [cite: 441]
    private static final String CORRELATION_ID_MDC_KEY = "correlationId"; // [cite: 442]

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER); // [cite: 450]

        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = generateCorrelationId(); // 
        }

        MDC.put(CORRELATION_ID_MDC_KEY, correlationId); // 
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId); // [cite: 462]

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear(); // [cite: 467]
        }
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString(); // [cite: 471]
    }
}