package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.Login.LoginRequest; // Corrigido
import com.deliverytech.delivery_api.dto.Login.LoginResponse; // Corrigido
import com.deliverytech.delivery_api.dto.Login.RegisterRequest; // Corrigido
import com.deliverytech.delivery_api.dto.Login.UserResponse; // Corrigido
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.security.JwtUtil;
import com.deliverytech.delivery_api.security.SecurityUtils;
import com.deliverytech.delivery_api.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getSenha())
            );
            UserDetails userDetails = authService.loadUserByUsername(loginRequest.getEmail());
            String token = jwtUtil.generateToken(userDetails);
            Usuario usuario = (Usuario) userDetails;
            UserResponse userResponse = new UserResponse(usuario);
            LoginResponse loginResponse = new LoginResponse(token, jwtExpiration, userResponse);
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno do servidor");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (authService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body("Email já está em uso");
            }
            Usuario novoUsuario = authService.criarUsuario(registerRequest);
            UserResponse userResponse = new UserResponse(novoUsuario);
            return ResponseEntity.status(201).body(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar usuário: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Usuario usuarioLogado = SecurityUtils.getCurrentUser();
            UserResponse userResponse = new UserResponse(usuarioLogado);
            return ResponseEntity.ok(userResponse);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Usuário não autenticado");
        }
    }
}






