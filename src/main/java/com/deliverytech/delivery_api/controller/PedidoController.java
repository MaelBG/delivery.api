package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponseWrapper;
import com.deliverytech.delivery_api.dto.PagedResponseWrapper;
import com.deliverytech.delivery_api.dto.CalculoPedidoDTO;
import com.deliverytech.delivery_api.dto.CalculoPedidoResponseDTO;
import com.deliverytech.delivery_api.dto.PedidoDTO;
import com.deliverytech.delivery_api.dto.PedidoResponseDTO;
import com.deliverytech.delivery_api.dto.StatusPedidoDTO;
import com.deliverytech.delivery_api.entity.StatusPedido;
import com.deliverytech.delivery_api.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*")
@Tag(name = "Pedidos", description = "Operações relacionadas aos pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Criar pedido", description = "Cria um novo pedido no sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente ou restaurante não encontrado"),
        @ApiResponse(responseCode = "409", description = "Produto indisponível")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> criarPedido(
            @Valid @RequestBody PedidoDTO dto) {
        PedidoResponseDTO pedido = pedidoService.criarPedido(dto);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido criado com sucesso");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{pedidoId}/itens")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    @Operation(summary = "Adicionar item ao pedido", description = "Adiciona um produto a um pedido existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Item adicionado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos, produto não disponível ou não pertence ao restaurante do pedido."),
        @ApiResponse(responseCode = "404", description = "Pedido ou produto não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> adicionarItem(
            @Parameter(description = "ID do pedido ao qual o item será adicionado.") @PathVariable Long pedidoId,
            @Parameter(description = "ID do produto a ser adicionado ao pedido.") @RequestParam Long produtoId,
            @Parameter(description = "Quantidade do produto a ser adicionada.") @RequestParam Integer quantidade) {
        PedidoResponseDTO pedido = pedidoService.adicionarItem(pedidoId, produtoId, quantidade);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Item adicionado com sucesso");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{pedidoId}/confirmar")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    @Operation(summary = "Confirmar pedido", description = "Confirma um pedido pendente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido confirmado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser confirmado (ex: já confirmado, sem itens)."),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> confirmarPedido(
            @Parameter(description = "ID do pedido a ser confirmado.") @PathVariable Long pedidoId) {
        PedidoResponseDTO pedido = pedidoService.confirmarPedido(pedidoId);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido confirmado com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar pedido por ID", description = "Recupera um pedido específico com todos os detalhes.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado."),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorId(
            @Parameter(description = "ID do pedido a ser buscado.") @PathVariable Long id) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorId(id);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido encontrado");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar pedidos", description = "Lista pedidos com filtros opcionais (status, data inicial e data final) e paginação.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista recuperada com sucesso.")
    })
    public ResponseEntity<PagedResponseWrapper<PedidoResponseDTO>> listar(
            @Parameter(description = "Status do pedido para filtro (PENDENTE, CONFIRMADO, etc.).") @RequestParam(required = false) StatusPedido status,
            @Parameter(description = "Data inicial para filtro de período (formato YYYY-MM-DD).")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final para filtro de período (formato YYYY-MM-DD).")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @Parameter(description = "Parâmetros de paginação") Pageable pageable) {

        LocalDateTime inicio = (dataInicio != null) ? dataInicio.atStartOfDay() : null;
        LocalDateTime fim = (dataFim != null) ? dataFim.atTime(23, 59, 59, 999999999) : null;

        Page<PedidoResponseDTO> pedidos = pedidoService.listarPedidos(status, inicio, fim, pageable);
        PagedResponseWrapper<PedidoResponseDTO> response =
                new PagedResponseWrapper<>(pedidos);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Buscar pedido por número", description = "Recupera um pedido específico pelo número do pedido.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado."),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> buscarPorNumero(
            @Parameter(description = "Número único do pedido a ser buscado.") @PathVariable String numeroPedido) {
        PedidoResponseDTO pedido = pedidoService.buscarPedidoPorNumero(numeroPedido);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Pedido encontrado");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANTE') or hasRole('ADMIN')")
    @Operation(summary = "Atualizar status do pedido", description = "Atualiza o status de um pedido, respeitando as transições válidas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado."),
        @ApiResponse(responseCode = "400", description = "Transição de status inválida.")
    })
    public ResponseEntity<ApiResponseWrapper<PedidoResponseDTO>> atualizarStatus(
            @Parameter(description = "ID do pedido a ter o status atualizado.") @PathVariable Long id,
            @Valid @RequestBody StatusPedidoDTO statusDTO) {
        PedidoResponseDTO pedido = pedidoService.atualizarStatusPedido(id, statusDTO);
        ApiResponseWrapper<PedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, pedido, "Status atualizado com sucesso");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE') or hasRole('ADMIN')")
    @Operation(summary = "Cancelar pedido", description = "Cancela um pedido se possível (não pode ser cancelado se já entregue).")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido cancelado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
        @ApiResponse(responseCode = "400", description = "Pedido não pode ser cancelado (ex: já entregue ou já cancelado).")
    })
    public ResponseEntity<Void> cancelarPedido(
            @Parameter(description = "ID do pedido a ser cancelado.") @PathVariable Long id,
            @Parameter(description = "Motivo do cancelamento (opcional).") @RequestParam(required = false) String motivo) {
        pedidoService.cancelarPedido(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CLIENTE') and @pedidoService.canAccess(#clienteId, 'CLIENTE'))")
    @Operation(summary = "Histórico do cliente", description = "Lista todos os pedidos de um cliente específico.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico recuperado com sucesso."),
        @ApiResponse(responseCode = "404", description = "Cliente não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorCliente(
            @Parameter(description = "ID do cliente para buscar o histórico de pedidos.") @PathVariable Long clienteId) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorCliente(clienteId);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Histórico recuperado com sucesso");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurante/{restauranteId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('RESTAURANTE') and @pedidoService.canAccess(#restauranteId, 'RESTAURANTE'))")
    @Operation(summary = "Pedidos do restaurante", description = "Lista todos os pedidos de um restaurante, com filtro de status opcional.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedidos recuperados com sucesso."),
        @ApiResponse(responseCode = "404", description = "Restaurante não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<List<PedidoResponseDTO>>> buscarPorRestaurante(
            @Parameter(description = "ID do restaurante para buscar os pedidos.") @PathVariable Long restauranteId,
            @Parameter(description = "Status do pedido para filtro (opcional).") @RequestParam(required = false) StatusPedido status) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidosPorRestaurante(restauranteId, status);
        ApiResponseWrapper<List<PedidoResponseDTO>> response =
                new ApiResponseWrapper<>(true, pedidos, "Pedidos recuperados com sucesso");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calcular")
    @Operation(summary = "Calcular total do pedido", description = "Calcula o total de um pedido com base nos itens fornecidos, sem salvá-lo.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total calculado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Dados inválidos."),
        @ApiResponse(responseCode = "404", description = "Produto ou restaurante não encontrado.")
    })
    public ResponseEntity<ApiResponseWrapper<CalculoPedidoResponseDTO>> calcularTotal(
            @Valid @RequestBody CalculoPedidoDTO dto) {
        CalculoPedidoResponseDTO calculo = pedidoService.calcularTotalPedido(dto);
        ApiResponseWrapper<CalculoPedidoResponseDTO> response =
                new ApiResponseWrapper<>(true, calculo, "Total calculado com sucesso");
        return ResponseEntity.ok(response);
    }
}