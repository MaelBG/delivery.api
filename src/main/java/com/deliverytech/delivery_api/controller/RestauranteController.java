package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponseWrapper;
import com.deliverytech.delivery_api.dto.PagedResponseWrapper;
import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO; // Adicione esta linha
import com.deliverytech.delivery_api.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
//import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/restaurantes") // O roteiro usa /api/restaurantes
@CrossOrigin(origins = "*")
@Tag(name = "Restaurantes", description = "Operações relacionadas aos restaurantes")
public class RestauranteController {

    @Autowired
    private RestauranteService restauranteService;

    /**
     * Cadastrar novo restaurante
     */
    @PostMapping
    @Operation(summary = "Cadastrar restaurante", description = "Cria um novo restaurante no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "Restaurante já existe")
    })
        public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> cadastrar(
        @Valid @org.springframework.web.bind.annotation.RequestBody // Use a do Spring para binding
        @io.swagger.v3.oas.annotations.parameters.RequestBody( // Use a do Swagger para documentação
            description = "Dados do restaurante a ser criado"
        ) RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.cadastrarRestaurante(dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Listar restaurantes com filtros e paginação
     */
    @GetMapping
    @Operation(summary = "Listar restaurantes", description = "Lista restaurantes com filtros opcionais e paginação")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso")
    })
    public ResponseEntity<PagedResponseWrapper<RestauranteResponseDTO>> listar(
            @Parameter(description = "Categoria do restaurante") @RequestParam(required = false) String categoria,
            @Parameter(description = "Status ativo do restaurante") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {
        Page<RestauranteResponseDTO> restaurantes =
                restauranteService.listarRestaurantes(categoria, ativo, pageable);
        PagedResponseWrapper<RestauranteResponseDTO> response =
                new PagedResponseWrapper<>(restaurantes);
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar restaurante por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar restaurante por ID", description = "Recupera um restaurante específico pelo ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurante encontrado"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> buscarPorId(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.buscarRestaurantePorId(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante encontrado");
        return ResponseEntity.ok(response);
    }

    /**
     * Atualizar restaurante
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar restaurante", description = "Atualiza os dados de um restaurante existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurante atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> atualizar(
            @Parameter(description = "ID do restaurante") @PathVariable Long id,
            @Valid @RequestBody RestauranteDTO dto) {
        RestauranteResponseDTO restaurante = restauranteService.atualizarRestaurante(id, dto);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Restaurante atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Ativar/Desativar restaurante
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Ativar/Desativar restaurante", description = "Alterna o status ativo/inativo do restaurante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status alterado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<RestauranteResponseDTO>> alterarStatus(
            @Parameter(description = "ID do restaurante") @PathVariable Long id) {
        RestauranteResponseDTO restaurante = restauranteService.alterarStatusRestaurante(id);
        ApiResponseWrapper<RestauranteResponseDTO> response =
                new ApiResponseWrapper<>(true, restaurante, "Status alterado com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Buscar restaurantes por categoria
     */
    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar por categoria", description = "Lista restaurantes de uma categoria específica")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurantes encontrados")
    })
    public ResponseEntity<ApiResponseWrapper<List<RestauranteResponseDTO>>> buscarPorCategoria(
            @Parameter(description = "Categoria do restaurante") @PathVariable String categoria) {
        List<RestauranteResponseDTO> restaurantes =
                restauranteService.buscarRestaurantesPorCategoria(categoria);
        ApiResponseWrapper<List<RestauranteResponseDTO>> response =
                new ApiResponseWrapper<>(true, restaurantes, "Restaurantes encontrados");
        return ResponseEntity.ok(response);
    }

    /**
     * Calcular taxa de entrega
     */
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
        ApiResponseWrapper<BigDecimal> response =
                new ApiResponseWrapper<>(true, taxa, "Taxa calculada com sucesso");
        return ResponseEntity.ok(response);
    }

    /**
     * Listar restaurantes próximos
     */
    @GetMapping("/proximos/{cep}")
    @Operation(summary = "Restaurantes próximos", description = "Lista restaurantes próximos a um CEP")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Restaurantes próximos encontrados")
    })
    public ResponseEntity<ApiResponseWrapper<List<RestauranteResponseDTO>>> buscarProximos(
            @Parameter(description = "CEP de referência") @PathVariable String cep,
            @Parameter(description = "Raio em km") @RequestParam(defaultValue = "10") Integer raio) {
        List<RestauranteResponseDTO> restaurantes =
                restauranteService.buscarRestaurantesProximos(cep, raio);
        ApiResponseWrapper<List<RestauranteResponseDTO>> response =
                new ApiResponseWrapper<>(true, restaurantes, "Restaurantes próximos encontrados");
        return ResponseEntity.ok(response);
    }

    /**
     * Listar produtos de um restaurante (Este endpoint está listado em RestauranteController no roteiro,
     * mas a lógica de produto pertence ao ProdutoService).
     */
    @GetMapping("/{restauranteId}/produtos")
    @Operation(summary = "Produtos do restaurante", description = "Lista todos os produtos de um restaurante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produtos encontrados"),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado")
    })
    public ResponseEntity<ApiResponseWrapper<List<ProdutoResponseDTO>>> buscarProdutosDoRestaurante(
            @Parameter(description = "ID do restaurante") @PathVariable Long restauranteId,
            @Parameter(description = "Filtrar apenas disponíveis") @RequestParam(required = false) Boolean disponivel) {
        // O roteiro indica que o produtoService.buscarProdutosPorRestaurante deve ser chamado.
        // Assumimos que o ProdutoService será injetado e este método existirá nele.
        // No momento, ProdutoService não está injetado aqui.
        // Para uma arquitetura limpa, este endpoint seria idealmente no ProdutoController.
        // Se mantido aqui, ProdutoService precisaria ser @Autowired.
        // List<ProdutoResponseDTO> produtos = produtoService.buscarProdutosPorRestaurante(restauranteId, disponivel);
        // ApiResponseWrapper<List<ProdutoResponseDTO>> response = new ApiResponseWrapper<>(true, produtos, "Produtos encontrados");
        // return ResponseEntity.ok(response);
        
        // Retorno de erro até que a injeção de ProdutoService seja feita ou o endpoint seja movido.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(ApiResponseWrapper.error("Este endpoint deve ser movido para ProdutoController ou ProdutoService deve ser injetado aqui."));
    }
}