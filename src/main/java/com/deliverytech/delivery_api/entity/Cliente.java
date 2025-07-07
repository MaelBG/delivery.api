package com.deliverytech.delivery_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore; // Importar JsonIgnore

@Entity
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private String endereco;
    private LocalDateTime dataCadastro;
    private boolean ativo;

    @JsonIgnore // <--- Adicione esta anotação
    @OneToMany(mappedBy = "cliente")
    private List<Pedido> pedidos;

    // Método para inativar o cliente (já adicionado anteriormente)
    public void inativar() {
        this.ativo = false;
    }
}