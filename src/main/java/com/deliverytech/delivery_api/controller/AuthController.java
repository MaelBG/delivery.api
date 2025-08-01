package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.Login.LoginRequest;
import com.deliverytech.delivery_api.dto.Login.LoginResponse;
import com.deliverytech.delivery_api.dto.Login.RegisterRequest;
import com.deliverytech.delivery_api.dto.Login.UserResponse;
import com.deliverytech.delivery_api.entity.Usuario;
import com.deliverytech.delivery_api.security.JwtUtil;
import com.deliverytech.delivery_api.security.SecurityUtils;
import com.deliverytech.delivery_api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Autenticação", description = "Operações de autenticação e autorização")
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
    @Operation(
        summary = "Fazer login",
        description = "Autentica um usuário e retorna um token JWT",
        tags = {"Autenticação"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login realizado com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    name = "Login bem-sucedido",
                    value = """
                            {
                              "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ...",
                              "tipo": "Bearer",
                              "expiracao": 86400000,
                              "usuario": {
                                "id": 1,
                                "nome": "João Silva",
                                "email": "joao@email.com",
                                "role": "CLIENTE"
                              }
                            }
                            """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<?> login(@Parameter(description = "Credenciais de login") @Valid @RequestBody LoginRequest loginRequest) {
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
    @Operation(
        summary = "Registrar novo usuário",
        description = "Cria uma nova conta de usuário no sistema",
        tags = {"Autenticação"}
    )
    public ResponseEntity<?> register(@Parameter(description = "Dados para criação da conta") @Valid @RequestBody RegisterRequest registerRequest) {
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
    @Operation(
        summary = "Obter dados do usuário logado",
        description = "Retorna os dados do usuário correspondente ao token JWT enviado.",
        security = @SecurityRequirement(name = "Bearer Authentication"),
        tags = {"Autenticação"}
    )
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