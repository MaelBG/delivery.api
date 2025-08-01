package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponseWrapper;
import com.deliverytech.delivery_api.dto.PagedResponseWrapper;
import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes")
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações relacionadas ao gerenciamento de restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar restaurante", description = "Cria um novo restaurante no sistema", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "409", description = "Restaurante já existe")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.cadastrarRestaurante(dto);
        ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, restaurante,
                "Restaurante criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar restaurantes", description = "Lista restaurantes com filtros opcionais e paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listar(
            @Parameter(description = "Categoria do restaurante") @RequestParam(required = false) String categoria,
            @Parameter(description = "Status ativo do restaurante") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes = restauranteService.listarRestaurantes(categoria, ativo, pageable);
        PagedResponseWrapper<RestauranteResponseDTO> response = new PagedResponseWrapper<>(restaurantes);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID", description = "Recupera um restaurante específico pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);
        ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, restaurante,
                "Restaurante encontrado");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
    @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.atualizarRestaurante(id, dto);
        ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, restaurante,
                "Restaurante atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @restauranteService.isOwner(#id))")
    @Operation(summary = "Ativar/Desativar restaurante", description = "Alterna o status ativo/inativo do restaurante", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatus(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.alterarStatusRestaurante(id);
        ApiResponseWrapper<RestauranteResponseDTO> response = new ApiResponseWrapper<>(true, restaurante,
                "Status alterado com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar por categoria", description = "Lista restaurantes de uma categoria específica")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurantes encontrados")
    })
    public ResponseEntity<ApiResponseWrapper<List<RestauranteResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoria do restaurante") @PathVariable String categoria) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesPorCategoria(categoria);
        ApiResponseWrapper<List<RestauranteResponseDTO>> response = new ApiResponseWrapper<>(true, restaurantes,
                "Restaurantes encontrados");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/taxa-entrega/{cep}")
    @Operation(summary = "Calcular taxa de entrega", description = "Calcula a taxa de entrega para um CEP específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Taxa calculada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<BigDecimal>> calcularTaxaEntrega(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Parameter(description = "CEP de destino") @PathVariable String cep) {
        BigDecimal taxa = restauranteService.calcularTaxaEntrega(id, cep);
        ApiResponseWrapper<BigDecimal> response = new ApiResponseWrapper<>(true, taxa, "Taxa calculada com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Restaurantes próximos", description = "Lista restaurantes próximos a um CEP")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurantes próximos encontrados")
    })
    public ResponseEntity<ApiResponseWrapper<List<RestauranteResponseDTO>>> buscarProximos(
            @Parameter(description = "CEP de referência") @PathVariable String cep,
            @Parameter(description = "Raio em km") @RequestParam(defaultValue = "10") Integer raio) {
        List<RestauranteResponseDTO> restaurantes = restauranteService.buscarRestaurantesProximos(cep, raio);
        ApiResponseWrapper<List<RestauranteResponseDTO>> response = new ApiResponseWrapper<>(true, restaurantes,
                "Restaurantes próximos encontrados");
        return ResponseEntity.ok(response);
    }
}