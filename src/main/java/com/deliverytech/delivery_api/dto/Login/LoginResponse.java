package com.deliverytech.delivery_api.dto.Login;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.deliverytech.delivery_api.dto.Login.UserResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo = "Bearer";
    private Long expiracao;
    private UserResponse usuario;

    // Construtor manual para a chamada específica do AuthController
    public LoginResponse(String token, Long expiracao, UserResponse usuario) { // 
        this.token = token;
        this.expiracao = expiracao;
        this.usuario = usuario;
    }
}