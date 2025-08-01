package com.deliverytech.delivery_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Schema(description = "Entidade que representa um pedido no sistema")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do pedido", example = "1")
    private Long id;

    @Schema(description = "Número único para identificação do pedido", example = "PED-ABC123D4")
    private String numeroPedido;

    @Schema(description = "Data e hora em que o pedido foi realizado", example = "2025-08-01T10:30:00")
    private LocalDateTime dataPedido;

    @Schema(description = "Endereço completo para a entrega do pedido", example = "Rua dos Bobos, nº 0")
    private String enderecoEntrega;

    @Schema(description = "Soma dos subtotais de todos os itens do pedido", example = "50.00")
    private BigDecimal subtotal;

    @Schema(description = "Valor da taxa de entrega", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Valor total do pedido (subtotal + taxa de entrega)", example = "55.00")
    private BigDecimal valorTotal;

    @Schema(description = "Observações adicionais do cliente para o pedido", example = "Sem cebola, por favor.")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Schema(description = "Status atual do pedido")
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @JsonIgnore
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();
    
    // Métodos da classe...
    // Construtor padrão e outros métodos... (mantidos como antes)
    public Pedido() {
        this.dataPedido = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.taxaEntrega = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
        this.status = StatusPedido.PENDENTE;
    }

    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this);
        calcularTotais();
    }

    public void removerItem(ItemPedido item) {
        if (this.itens != null) {
            this.itens.remove(item);
            item.setPedido(null);
            calcularTotais();
        }
    }

    public void calcularTotais() {
        this.subtotal = this.itens.stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (this.restaurante != null && this.restaurante.getTaxaEntrega() != null) {
            this.taxaEntrega = this.restaurante.getTaxaEntrega();
        } else {
            this.taxaEntrega = BigDecimal.ZERO;
        }

        this.valorTotal = this.subtotal.add(this.taxaEntrega);
    }

    public void confirmar() {
        if (this.itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item para ser confirmado.");
        }
        this.setStatus(StatusPedido.CONFIRMADO);
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}