package com.deliverytech.delivery_api.dto.Login;

import com.deliverytech.delivery_api.entity.Role;
import com.deliverytech.delivery_api.entity.Usuario;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class UserResponse {
    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private Long restauranteld;

    public UserResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.role = usuario.getRole();
        this.ativo = usuario.getAtivo();
        this.dataCriacao = usuario.getDataCriacao();
        this.restauranteld = usuario.getRestauranteld();
    }
}