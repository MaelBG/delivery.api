package com.deliverytech.delivery_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Schema(description = "Entidade que representa um produto de um restaurante")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do produto", example = "101")
    private Long id;

    @Schema(description = "Nome do produto", example = "Pizza Margherita")
    private String nome;

    @Schema(description = "Descrição detalhada do produto", example = "Molho de tomate fresco, mussarela de búfala e manjericão.")
    private String descricao;

    @Schema(description = "Preço do produto", example = "35.90")
    private BigDecimal preco;

    @Schema(description = "Categoria do produto", example = "Pizza")
    private String categoria;

    @Schema(description = "Indica se o produto está disponível para venda", example = "true")
    private boolean disponivel;

    @ManyToOne
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @OneToMany(mappedBy = "produto")
    @Schema(hidden = true)
    private List<ItemPedido> itensPedido;
}