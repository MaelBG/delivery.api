package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar JsonIgnore

@Entity
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroPedido;
    private LocalDateTime dataPedido;
    private String enderecoEntrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String observacoes;

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @JsonIgnore // <--- Adicione esta anotação
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

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