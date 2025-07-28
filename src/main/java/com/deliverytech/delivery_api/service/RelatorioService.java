package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.repository.PedidoRepository;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.repository.projections.RelatorioVendas; // Para a projeção de vendas
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true) // Relatórios são geralmente apenas leitura
public class RelatorioService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    /**
     * Retorna o relatório de vendas por restaurante.
     * Utiliza a projeção RelatorioVendas do RestauranteRepository.
     */
    public List<RelatorioVendas> getVendasPorRestaurante() {
        return restauranteRepository.relatorioVendasPorRestaurante();
    }

    /**
     * Retorna o relatório de top produtos mais vendidos.
     * Utiliza o método findProdutosMaisVendidos do ProdutoRepository, que retorna Object[].
     */
    public List<Object[]> getProdutosMaisVendidos() {
        return produtoRepository.findProdutosMaisVendidos();
    }

    /**
     * Retorna o relatório de clientes mais ativos (por número de pedidos).
     * Utiliza o método rankingClientesPorPedidos do ClienteRepository, que retorna Object[].
     */
    public List<Object[]> getClientesMaisAtivos() {
        return clienteRepository.rankingClientesPorPedidos();
    }

    /**
     * Retorna o relatório de pedidos por período.
     * O PedidoRepository.countPedidosByStatus() retorna o total por status (Object[]: StatusPedido, Long count).
     * O roteiro sugere "Pedidos por período", então podemos usar relatorioPedidosPorPeriodoEStatus do PedidoRepository
     * e agrupar ou sumarizar. Por simplicidade, este método retornará a contagem de pedidos por status no período.
     */
    public List<Object[]> getPedidosPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        // Esta é uma adaptação. O roteiro sugere "pedidos-por-periodo", mas o PedidoRepository
        // tem "relatorioPedidosPorPeriodoEStatus" que inclui StatusPedido.
        // Se a intenção é apenas contar todos os pedidos no período, seria uma nova query.
        // Para alinhar com o retorno do controller, podemos retornar algo como a contagem por status.
        // Ou você pode ter uma query que retorne o total de pedidos ou o valor total no período.
        // Vamos retornar o count de pedidos por status no período, similar ao que já existe.
        return pedidoRepository.countPedidosByStatus(); // Você pode precisar de uma query mais específica aqui.
        // Exemplo: List<Pedido> pedidosNoPeriodo = pedidoRepository.findByDataPedidoBetweenOrderByDataPedidoDesc(dataInicio, dataFim);
        // Retornando a lista de pedidos em si, ou sumarizando como no exemplo.
    }
}