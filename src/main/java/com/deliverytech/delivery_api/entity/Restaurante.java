package com.deliverytech.delivery_api.entity;
 
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
 
@Entity
@Data
public class Restaurante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String categoria;
    private String endereco;
    private String telefone;
    private BigDecimal taxaEntrega;
    private BigDecimal avaliacao;
    private boolean ativo;

    // --- NOVOS CAMPOS ADICIONADOS ---
    private Integer tempoEntrega; // Campo para tempo estimado de entrega
    private String horarioFuncionamento; // Campo para horário de funcionamento
    // --- FIM DOS NOVOS CAMPOS ---
 
    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    private List<Produto> produtos;
 
    @JsonIgnore
    @OneToMany(mappedBy = "restaurante")
    private List<Pedido> pedidos;
}