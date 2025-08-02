package com.deliverytech.delivery_api.service;

import io.micrometer.core.instrument.Counter; // <-- ADICIONE ESTA LINHA
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer; // <-- ADICIONE ESTA LINHA
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@EnableScheduling
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    private final MeterRegistry meterRegistry;

    // Limites para os alertas
    private static final double ERROR_RATE_THRESHOLD = 0.05; // 5%
    private static final double RESPONSE_TIME_THRESHOLD = 1000; // 1 segundo em ms
    private static final double CPU_THRESHOLD = 0.8; // 80%

    public AlertService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 60 segundos
    public void verificarAlertas() {
        verificarErrorRate();
        verificarResponseTime();
        verificarRecursosSistema();
    }

    private void verificarErrorRate() {
        double totalRequests = getCounterValue("delivery.pedidos.total");
        double errorRequests = getCounterValue("delivery.pedidos.erro");

        if (totalRequests > 10) { // Só alerta se houver um número mínimo de requisições
            double errorRate = errorRequests / totalRequests;
            if (errorRate > ERROR_RATE_THRESHOLD) {
                enviarAlerta("HIGH_ERROR_RATE",
                        String.format("Taxa de erro alta: %.2f%%", errorRate * 100), "CRITICAL");
            }
        }
    }

    private void verificarResponseTime() {
        double avgResponseTime = getTimerMean("delivery.pedido.processamento.tempo");
        if (avgResponseTime > RESPONSE_TIME_THRESHOLD) {
            enviarAlerta("HIGH_RESPONSE_TIME",
                    String.format("Tempo de resposta alto: %.2fms", avgResponseTime), "WARNING");
        }
    }

    private void verificarRecursosSistema() {
        double cpuUsage = getGaugeValue("system.cpu.usage");
        if (cpuUsage > CPU_THRESHOLD) {
            enviarAlerta("HIGH_CPU_USAGE",
                    String.format("Uso de CPU alto: %.2f%%", cpuUsage * 100), "CRITICAL");
        }
    }

    private void enviarAlerta(String tipo, String mensagem, String severidade) {
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("tipo", tipo);
        alerta.put("mensagem", mensagem);
        alerta.put("severidade", severidade);
        alerta.put("aplicacao", "delivery-api");

        // Log do alerta (simulando o envio para um sistema de notificação)
        logger.warn("ALERTA [{}] {}: {}", severidade, tipo, mensagem);
    }

    // Métodos auxiliares para buscar valores do MeterRegistry
    private double getCounterValue(String name) {
        Counter counter = meterRegistry.find(name).counter();
        return (counter != null) ? counter.count() : 0.0;
    }

    private double getTimerMean(String name) {
        Timer timer = meterRegistry.find(name).timer();
        return (timer != null) ? timer.mean(java.util.concurrent.TimeUnit.MILLISECONDS) : 0.0;
    }

    private double getGaugeValue(String name) {
        io.micrometer.core.instrument.Gauge gauge = meterRegistry.find(name).gauge();
        return (gauge != null) ? gauge.value() : 0.0;
    }
}