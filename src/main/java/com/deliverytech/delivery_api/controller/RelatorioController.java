package com.deliverytech.delivery_api.controller;

import com.deliverytech.delivery_api.dto.ApiResponseWrapper;
import com.deliverytech.delivery_api.repository.projections.RelatorioVendas;
import com.deliverytech.delivery_api.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


// Por enquanto, usamos os tipos de retorno dos repositórios (projeções ou Object[]).

@RestController
@RequestMapping("/api/relatorios") // O roteiro usa /api/relatorios
@CrossOrigin(origins = "*")
@Tag(name = "Relatórios", description = "Endpoints para geração de relatórios do sistema")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    /**
     * Relatório de Vendas por Restaurante
     */
    @GetMapping("/vendas-por-restaurante")
    @Operation(summary = "Vendas por restaurante", description = "Gera um relatório de vendas totais por restaurante.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso.")
    })
    public ResponseEntity<ApiResponseWrapper<List<RelatorioVendas>>> getVendasPorRestaurante() {
        List<RelatorioVendas> relatorio = relatorioService.getVendasPorRestaurante();
        ApiResponseWrapper<List<RelatorioVendas>> response =
                new ApiResponseWrapper<>(true, relatorio, "Relatório de vendas por restaurante gerado com sucesso.");
        return ResponseEntity.ok(response);
    }

    /**
     * Relatório de Top Produtos Mais Vendidos
     */
    @GetMapping("/produtos-mais-vendidos")
    @Operation(summary = "Top produtos mais vendidos", description = "Lista os produtos mais vendidos no sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso.")
    })
    public ResponseEntity<ApiResponseWrapper<List<Object[]>>> getProdutosMaisVendidos() {
        List<Object[]> relatorio = relatorioService.getProdutosMaisVendidos();
        ApiResponseWrapper<List<Object[]>> response =
                new ApiResponseWrapper<>(true, relatorio, "Relatório de produtos mais vendidos gerado com sucesso.");
        return ResponseEntity.ok(response);
    }

    /**
     * Relatório de Clientes Mais Ativos (por número de pedidos)
     */
    @GetMapping("/clientes-ativos")
    @Operation(summary = "Clientes mais ativos", description = "Lista os clientes com mais pedidos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso.")
    })
    public ResponseEntity<ApiResponseWrapper<List<Object[]>>> getClientesMaisAtivos() {
        List<Object[]> relatorio = relatorioService.getClientesMaisAtivos();
        ApiResponseWrapper<List<Object[]>> response =
                new ApiResponseWrapper<>(true, relatorio, "Relatório de clientes mais ativos gerado com sucesso.");
        return ResponseEntity.ok(response);
    }

    /**
     * Relatório de Pedidos por Período
     */
    @GetMapping("/pedidos-por-periodo")
    @Operation(summary = "Pedidos por período", description = "Gera um relatório de pedidos dentro de um período de datas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Relatório gerado com sucesso."),
        @ApiResponse(responseCode = "400", description = "Datas inválidas.")
    })
    public ResponseEntity<ApiResponseWrapper<List<Object[]>>> getPedidosPorPeriodo(
            @Parameter(description = "Data inicial (formato YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @Parameter(description = "Data final (formato YYYY-MM-DD)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(23, 59, 59, 999999999);

        List<Object[]> relatorio = relatorioService.getPedidosPorPeriodo(inicio, fim);
        ApiResponseWrapper<List<Object[]>> response =
                new ApiResponseWrapper<>(true, relatorio, "Relatório de pedidos por período gerado com sucesso.");
        return ResponseEntity.ok(response);
    }
}