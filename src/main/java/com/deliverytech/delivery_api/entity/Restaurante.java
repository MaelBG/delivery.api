package com.deliverytech.delivery_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Schema(description = "Entidade que representa um restaurante no sistema")
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único do restaurante", example = "1")
    private Long id;

    @Schema(description = "Nome do restaurante", example = "Pizza Palace")
    private String nome;

    @Schema(description = "Categoria/tipo de culinária do restaurante", example = "Italiana")
    private String categoria;

    @Schema(description = "Endereço completo do restaurante", example = "Rua das Pizzas, 123 - Centro, São Paulo - SP")
    private String endereco;

    @Schema(description = "Telefone de contato do restaurante", example = "(11) 1234-5678")
    private String telefone;

    @Schema(description = "Taxa de entrega do restaurante", example = "5.00")
    private BigDecimal taxaEntrega;

    @Schema(description = "Avaliação média do restaurante (0 a 5 estrelas)", example = "4.5", minimum = "0", maximum = "5")
    private BigDecimal avaliacao;

    @Schema(description = "Indica se o restaurante está ativo no sistema", example = "true", defaultValue = "true")
    private boolean ativo;

    @Schema(description = "Tempo estimado de entrega em minutos", example = "45")
    private Integer tempoEntrega;

    @Schema(description = "Horário de funcionamento", example = "18:00 - 23:00")
    private String horarioFuncionamento;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    @Schema(description = "Lista de produtos oferecidos pelo restaurante")
    private List<Produto> produtos;

    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    @Schema(description = "Lista de pedidos recebidos pelo restaurante")
    private List<Pedido> pedidos;
}