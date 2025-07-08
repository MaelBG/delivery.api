package com.deliverytech.delivery_api.entity;
 
import jakarta.persistence.*; 
import lombok.Data; 
import java.math.BigDecimal; 
import java.util.List; 
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar JsonIgnore
 
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
 
    @JsonIgnore // <--- Adicione esta anotação
    @OneToMany(mappedBy = "restaurante") 
    private List<Produto> produtos; 
 
    @JsonIgnore // <--- Também é uma boa prática adicionar aqui, se Pedidos não for sempre necessário junto com Restaurante
    @OneToMany(mappedBy = "restaurante") 
    private List<Pedido> pedidos; 
}