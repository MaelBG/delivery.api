package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList; // Importar ArrayList
import java.util.List;

@Entity
@Data
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroPedido; // Adicionado: Campo para o número do pedido
    private LocalDateTime dataPedido;
    private String enderecoEntrega;
    private BigDecimal subtotal;
    private BigDecimal taxaEntrega;
    private BigDecimal valorTotal;
    private String observacoes; // Adicionado: Campo para observações

    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true) // Adicionado orphanRemoval
    private List<ItemPedido> itens = new ArrayList<>(); // Inicializado aqui para evitar NullPointerException

    // Construtor padrão para inicializar campos e listas
    public Pedido() {
        this.dataPedido = LocalDateTime.now();
        this.subtotal = BigDecimal.ZERO;
        this.taxaEntrega = BigDecimal.ZERO;
        this.valorTotal = BigDecimal.ZERO;
        this.status = StatusPedido.PENDENTE; // Definir status inicial
    }

    // Método para adicionar um item ao pedido
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        item.setPedido(this); // Garante a ligação bidirecional
        calcularTotais();
    }

    // Método para remover um item do pedido (boa prática)
    public void removerItem(ItemPedido item) {
        this.itens.remove(item);
        item.setPedido(null); // Remove a ligação bidirecional
        calcularTotais();
    }

    // Método para calcular os totais do pedido (subtotal, taxa, valor total)
    public void calcularTotais() {
        this.subtotal = this.itens.stream()
            .map(ItemPedido::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // A taxa de entrega será definida na criação do pedido ou pode vir do restaurante
        // Por agora, vamos garantir que ela seja somada ao valor total
        this.valorTotal = this.subtotal.add(this.taxaEntrega != null ? this.taxaEntrega : BigDecimal.ZERO);
    }

    // Método para confirmar o pedido (chamado pelo PedidoService)
    public void confirmar() {
        if (this.itens.isEmpty()) {
            throw new IllegalArgumentException("Pedido deve ter pelo menos um item para ser confirmado.");
        }
        this.setStatus(StatusPedido.CONFIRMADO);
        // Outras lógicas de negócio ao confirmar, se houver
    }

    // Método para atualizar o endereço de entrega (se necessário)
    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    // Certifique-se de que o getter para 'observacoes' existe se for usado no service
    public String getObservacoes() {
        return observacoes;
    }

    // E o setter correspondente
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}