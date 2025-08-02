package com.deliverytech.delivery_api.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {

    private final Counter pedidosProcessados;
    private final Counter pedidosComSucesso;
    private final Counter pedidosComErro;
    private final Timer tempoProcessamentoPedido;

    public MetricsService(MeterRegistry meterRegistry) {
        // Contador para o total de pedidos processados
        this.pedidosProcessados = Counter.builder("delivery.pedidos.total")
            .description("Total de pedidos processados") // [cite: 243]
            .register(meterRegistry); // [cite: 244]

        // Contador para pedidos bem-sucedidos
        this.pedidosComSucesso = Counter.builder("delivery.pedidos.sucesso")
            .description("Pedidos processados com sucesso") // [cite: 246]
            .register(meterRegistry); // [cite: 247]

        // Contador para pedidos com erro
        this.pedidosComErro = Counter.builder("delivery.pedidos.erro")
            .description("Pedidos com erro no processamento") // [cite: 249]
            .register(meterRegistry); // [cite: 250]

        // Timer para medir o tempo de processamento dos pedidos
        this.tempoProcessamentoPedido = Timer.builder("delivery.pedido.processamento.tempo")
            .description("Tempo de processamento de pedidos") // [cite: 258]
            .register(meterRegistry); // [cite: 259]
    }

    public void incrementarPedidosProcessados() {
        pedidosProcessados.increment(); // [cite: 272]
    }

    public void incrementarPedidosComSucesso() {
        pedidosComSucesso.increment(); // [cite: 275]
    }

    public void incrementarPedidosComErro() {
        pedidosComErro.increment(); // [cite: 278]
    }

    public Timer.Sample iniciarTimerPedido() {
        return Timer.start();
    }

    public void finalizarTimerPedido(Timer.Sample sample) {
        sample.stop(tempoProcessamentoPedido);
    }
}