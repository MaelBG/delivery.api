package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List; // Importe esta classe

@Entity
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private String categoria;
    private boolean disponivel;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    // ESTA É A PROPRIEDADE QUE ESTÁ FALTANDO OU COM PROBLEMAS
    @OneToMany(mappedBy = "produto") // 'produto' deve corresponder ao nome do campo na entidade ItemPedido
    private List<ItemPedido> itensPedido;
}