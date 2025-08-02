package com.deliverytech.delivery_api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT"); // [cite: 377]
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void logUserAction(String userId, String action, String resource, Object details) { // 
        try {
            Map<String, Object> auditEvent = new HashMap<>(); // [cite: 381]
            auditEvent.put("timestamp", LocalDateTime.now().toString()); // [cite: 382]
            auditEvent.put("userId", userId); // [cite: 383]
            auditEvent.put("action", action); // [cite: 384]
            auditEvent.put("resource", resource); // [cite: 385]
            auditEvent.put("details", details); // [cite: 386]
            auditEvent.put("correlationId", MDC.get("correlationId")); // [cite: 388]

            String jsonLog = objectMapper.writeValueAsString(auditEvent);
            auditLogger.info(jsonLog); // [cite: 391]
        } catch (Exception e) {
            auditLogger.error("Erro ao registrar evento de auditoria", e); // [cite: 393]
        }
    }

    public void logSecurityEvent(String userId, String event, String details, boolean success) { // [cite: 414]
        try {
            Map<String, Object> securityEvent = new HashMap<>(); // [cite: 415]
            securityEvent.put("timestamp", LocalDateTime.now().toString()); // [cite: 416]
            securityEvent.put("userId", userId); // [cite: 417]
            securityEvent.put("event", event); // [cite: 418]
            securityEvent.put("details", details); // [cite: 419]
            securityEvent.put("success", success); // [cite: 420]
            securityEvent.put("correlationId", MDC.get("correlationId")); // [cite: 421]

            String jsonLog = objectMapper.writeValueAsString(securityEvent);
            auditLogger.info(jsonLog); // [cite: 426]
        } catch (Exception e) {
            auditLogger.error("Erro ao registrar evento de segurança", e); // [cite: 428]
        }
    }
}