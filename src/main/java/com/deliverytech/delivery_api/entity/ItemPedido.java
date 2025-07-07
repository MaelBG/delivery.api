package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*; 
import lombok.Data; 
import java.math.BigDecimal; 
 
@Entity
@Data

public class ItemPedido { 
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    private int quantidade; 
    private BigDecimal precoUnitario; 
    private BigDecimal subtotal; 
 
    @ManyToOne 
    @JoinColumn(name = "pedido_id") 
    private Pedido pedido; 
 
    @ManyToOne 
    @JoinColumn(name = "produto_id") 
    private Produto produto; 

    // Método para calcular o subtotal do item
    public void calcularSubtotal() {
        if (this.precoUnitario != null) {
            this.subtotal = this.precoUnitario.multiply(BigDecimal.valueOf(this.quantidade));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }
}