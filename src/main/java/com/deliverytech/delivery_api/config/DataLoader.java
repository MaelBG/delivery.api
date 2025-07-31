package com.deliverytech.delivery_api.config; // Ajuste o pacote se necessário

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import com.deliverytech.delivery_api.repository.projections.RelatorioVendas; // Importe a projeção
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile; // Importe esta anotação

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Profile("!test") // Adicione esta linha para desabilitar no perfil de teste
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE ==="); //

        // Limpar dados existentes (boa prática para testes)
        pedidoRepository.deleteAll(); //
        produtoRepository.deleteAll(); //
        restauranteRepository.deleteAll(); //
        clienteRepository.deleteAll(); //

        // Inserir dados de teste
        inserirClientes(); //
        inserirRestaurantes(); //
        inserirProdutos(); // Implemente este método com seus dados
        inserirPedidos();  // Implemente este método com seus dados

        // Executar testes das consultas
        testarConsultas(); //

        System.out.println("=== CARGA DE DADOS CONCLUÍDA ==="); //
    }

    // --- Métodos de Inserção de Dados ---

    private void inserirClientes() {
        System.out.println("--- Inserindo Clientes ---"); //
        Cliente cliente1 = new Cliente();
        cliente1.setNome("João Silva"); //
        cliente1.setEmail("joao@email.com"); //
        cliente1.setTelefone("11999999999"); //
        cliente1.setEndereco("Rua A, 123"); //
        cliente1.setAtivo(true); //

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Maria Santos"); //
        cliente2.setEmail("maria@email.com"); //
        cliente2.setTelefone("11888888888"); //
        cliente2.setEndereco("Rua B, 456"); //
        cliente2.setAtivo(true); //

        Cliente cliente3 = new Cliente();
        cliente3.setNome("Pedro Oliveira"); //
        cliente3.setEmail("pedro@email.com"); //
        cliente3.setTelefone("11777777777"); //
        cliente3.setEndereco("Rua C, 789"); //
        cliente3.setAtivo(false); //

        clienteRepository.saveAll(Arrays.asList(cliente1, cliente2, cliente3)); //
        System.out.println("3 clientes inseridos"); //
    }

    private void inserirRestaurantes() {
        System.out.println("--- Inserindo Restaurantes ---"); //
        Restaurante restaurante1 = new Restaurante();
        restaurante1.setNome("Pizza Express"); //
        restaurante1.setCategoria("Italiana"); //
        restaurante1.setEndereco("Av. Principal, 100"); //
        restaurante1.setTelefone("1133333333"); //
        restaurante1.setTaxaEntrega(new BigDecimal("3.50")); //
        restaurante1.setAtivo(true); //
        restaurante1.setAvaliacao(new BigDecimal("4.5"));

        Restaurante restaurante2 = new Restaurante();
        restaurante2.setNome("Burger King"); //
        restaurante2.setCategoria("Fast Food"); //
        restaurante2.setEndereco("Rua Central, 200"); //
        restaurante2.setTelefone("1144444444"); //
        restaurante2.setTaxaEntrega(new BigDecimal("5.00")); //
        restaurante2.setAtivo(true); //
        restaurante2.setAvaliacao(new BigDecimal("4.0"));

        restauranteRepository.saveAll(Arrays.asList(restaurante1, restaurante2)); //
        System.out.println("2 restaurantes inseridos"); //
    }

    private void inserirProdutos() {
        System.out.println("--- Inserindo Produtos ---");
        // Implemente a inserção de pelo menos 5 produtos aqui, seguindo o padrão acima.
        // Certifique-se de associar os produtos aos restaurantes criados.
        // Você pode se basear no conteúdo do arquivo data.sql para exemplos.
        // Exemplo:
        Restaurante pizzaExpress = restauranteRepository.findByNome("Pizza Express").orElse(null);
        Restaurante burgerKing = restauranteRepository.findByNome("Burger King").orElse(null);

        if (pizzaExpress != null && burgerKing != null) {
            Produto produto1 = new Produto();
            produto1.setNome("Pizza Margherita");
            produto1.setDescricao("Molho de tomate, mussarela e manjericão");
            produto1.setPreco(new BigDecimal("35.90"));
            produto1.setCategoria("Pizza");
            produto1.setDisponivel(true);
            produto1.setRestaurante(pizzaExpress);

            Produto produto2 = new Produto();
            produto2.setNome("Pizza Calabresa");
            produto2.setDescricao("Molho de tomate, mussarela e calabresa");
            produto2.setPreco(new BigDecimal("38.90"));
            produto2.setCategoria("Pizza");
            produto2.setDisponivel(true);
            produto2.setRestaurante(pizzaExpress);

            Produto produto3 = new Produto();
            produto3.setNome("X-Burger");
            produto3.setDescricao("Hambúrguer, queijo, alface e tomate");
            produto3.setPreco(new BigDecimal("18.90"));
            produto3.setCategoria("Hamburguer");
            produto3.setDisponivel(true);
            produto3.setRestaurante(burgerKing);

            Produto produto4 = new Produto();
            produto4.setNome("X-Bacon");
            produto4.setDescricao("Hambúrguer, queijo, bacon, alface e tomate");
            produto4.setPreco(new BigDecimal("22.90"));
            produto4.setCategoria("Hamburguer");
            produto4.setDisponivel(true);
            produto4.setRestaurante(burgerKing);

            Produto produto5 = new Produto();
            produto5.setNome("Batata Frita");
            produto5.setDescricao("Porção de batata frita crocante");
            produto5.setPreco(new BigDecimal("12.90"));
            produto5.setCategoria("Acompanhamento");
            produto5.setDisponivel(true);
            produto5.setRestaurante(burgerKing);

            produtoRepository.saveAll(Arrays.asList(produto1, produto2, produto3, produto4, produto5));
            System.out.println("5 produtos inseridos");
        } else {
            System.out.println("Restaurantes não encontrados para inserir produtos.");
        }
    }

    private void inserirPedidos() {
        System.out.println("--- Inserindo Pedidos ---");
        // Implemente a inserção de pelo menos 2 pedidos aqui.
        // Certifique-se de associar os pedidos a clientes e restaurantes, e adicionar itens aos pedidos.
        // Você pode se basear no conteúdo do arquivo data.sql para exemplos.
        // Exemplo:
        Cliente joao = clienteRepository.findByEmail("joao@email.com").orElse(null);
        Cliente maria = clienteRepository.findByEmail("maria@email.com").orElse(null);
        Restaurante pizzaExpress = restauranteRepository.findByNome("Pizza Express").orElse(null);
        Restaurante burgerKing = restauranteRepository.findByNome("Burger King").orElse(null);

        if (joao != null && maria != null && pizzaExpress != null && burgerKing != null) {
            Produto pizzaMargherita = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("Pizza Margherita").stream().findFirst().orElse(null);
            Produto xBurger = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("X-Burger").stream().findFirst().orElse(null);
            Produto batataFrita = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue("Batata Frita").stream().findFirst().orElse(null);

            if (pizzaMargherita != null && xBurger != null && batataFrita != null) {
                // Pedido 1
                Pedido pedido1 = new Pedido();
                pedido1.setCliente(joao);
                pedido1.setRestaurante(pizzaExpress);
                pedido1.setStatus(StatusPedido.PENDENTE);
                pedido1.setNumeroPedido("PED12345");
                pedido1.setEnderecoEntrega(joao.getEndereco());
                pedido1.setTaxaEntrega(pizzaExpress.getTaxaEntrega());

                ItemPedido item1_1 = new ItemPedido();
                item1_1.setProduto(pizzaMargherita);
                item1_1.setQuantidade(1);
                item1_1.setPrecoUnitario(pizzaMargherita.getPreco());
                item1_1.calcularSubtotal();
                pedido1.adicionarItem(item1_1);
                pedido1.calcularTotais();
                pedidoRepository.save(pedido1);


                // Pedido 2
                Pedido pedido2 = new Pedido();
                pedido2.setCliente(maria);
                pedido2.setRestaurante(burgerKing);
                pedido2.setStatus(StatusPedido.CONFIRMADO);
                pedido2.setNumeroPedido("PED67890");
                pedido2.setEnderecoEntrega(maria.getEndereco());
                pedido2.setTaxaEntrega(burgerKing.getTaxaEntrega());

                ItemPedido item2_1 = new ItemPedido();
                item2_1.setProduto(xBurger);
                item2_1.setQuantidade(1);
                item2_1.setPrecoUnitario(xBurger.getPreco());
                item2_1.calcularSubtotal();
                pedido2.adicionarItem(item2_1);

                ItemPedido item2_2 = new ItemPedido();
                item2_2.setProduto(batataFrita);
                item2_2.setQuantidade(1);
                item2_2.setPrecoUnitario(batataFrita.getPreco());
                item2_2.calcularSubtotal();
                pedido2.adicionarItem(item2_2);
                pedido2.calcularTotais();
                pedidoRepository.save(pedido2);

                System.out.println("2 pedidos inseridos");
            } else {
                System.out.println("Produtos necessários para pedidos não encontrados.");
            }
        } else {
            System.out.println("Clientes ou restaurantes não encontrados para inserir pedidos.");
        }
    }

    // --- Método de Teste de Consultas ---

    private void testarConsultas() {
        System.out.println("\n=== TESTANDO CONSULTAS DOS REPOSITORIES ==="); //

        // Teste ClienteRepository
        System.out.println("\n--- Testes ClienteRepository ---"); //
        var clientePorEmail = clienteRepository.findByEmail("joao@email.com"); //
        System.out.println("Cliente por email: " +
                (clientePorEmail.isPresent() ? clientePorEmail.get().getNome() : "Não encontrado")); //

        var clientesAtivos = clienteRepository.findByAtivoTrue(); //
        System.out.println("Clientes ativos: " + clientesAtivos.size()); //

        var clientesPorNome = clienteRepository.findByNomeContainingIgnoreCase("silva"); //
        System.out.println("Clientes com 'silva' no nome: " + clientesPorNome.size()); //

        boolean emailExiste = clienteRepository.existsByEmail("maria@email.com"); //
        System.out.println("Email maria@email.com existe: " + emailExiste); //

        List<Object[]> rankingClientes = clienteRepository.rankingClientesPorPedidos();
        System.out.println("Ranking de Clientes por Pedidos: " + rankingClientes.size());
        rankingClientes.forEach(obj -> System.out.println("  Nome: " + obj[0] + ", Total Pedidos: " + obj[1]));


        System.out.println("\n--- Testes RestauranteRepository ---");
        List<Restaurante> restaurantesItalianos = restauranteRepository.findByCategoriaAndAtivoTrue("Italiana");
        System.out.println("Restaurantes Italianos ativos: " + restaurantesItalianos.size());

        List<Restaurante> restaurantesTaxaBaixa = restauranteRepository.findByTaxaEntregaLessThanEqual(new BigDecimal("4.00"));
        System.out.println("Restaurantes com taxa <= 4.00: " + restaurantesTaxaBaixa.size());

        List<Restaurante> top5Restaurantes = restauranteRepository.findTop5ByOrderByNomeAsc();
        System.out.println("Top 5 Restaurantes por Nome (alfabético): " + top5Restaurantes.size());
        top5Restaurantes.forEach(r -> System.out.println("  - " + r.getNome()));

        List<RelatorioVendas> relatorioVendas = restauranteRepository.relatorioVendasPorRestaurante();
        System.out.println("Relatório de Vendas por Restaurante: " + relatorioVendas.size());
        relatorioVendas.forEach(rv -> System.out.println("  Restaurante: " + rv.getNomeRestaurante() + ", Total Vendas: " + rv.getTotalVendas() + ", Qtd Pedidos: " + rv.getQuantidePedidos()));


        System.out.println("\n--- Testes ProdutoRepository ---");
        List<Produto> produtosPizzaExpress = produtoRepository.findByRestauranteIdAndDisponivelTrue(
            restauranteRepository.findByNome("Pizza Express").orElseThrow().getId());
        System.out.println("Produtos da Pizza Express disponíveis: " + produtosPizzaExpress.size());

        List<Produto> produtosDisponiveis = produtoRepository.findByDisponivelTrue();
        System.out.println("Total de produtos disponíveis: " + produtosDisponiveis.size());

      

        System.out.println("\n--- Testes PedidoRepository ---");
        List<Pedido> pedidosJoao = pedidoRepository.findByClienteIdOrderByDataPedidoDesc(
            clienteRepository.findByEmail("joao@email.com").orElseThrow().getId());
        System.out.println("Pedidos do João Silva: " + pedidosJoao.size());

        List<Pedido> pedidosPendentes = pedidoRepository.findByStatusOrderByDataPedidoDesc(StatusPedido.PENDENTE);
        System.out.println("Pedidos Pendentes: " + pedidosPendentes.size());

        List<Pedido> top10Pedidos = pedidoRepository.findTop10ByOrderByDataPedidoDesc();
        System.out.println("Os 10 pedidos mais recentes: " + top10Pedidos.size());

        LocalDateTime inicioPeriodo = LocalDateTime.now().minusDays(10);
        LocalDateTime fimPeriodo = LocalDateTime.now();
        List<Pedido> pedidosNoPeriodo = pedidoRepository.findByDataPedidoBetweenOrderByDataPedidoDesc(inicioPeriodo, fimPeriodo);
        System.out.println("Pedidos nos últimos 10 dias: " + pedidosNoPeriodo.size());

        List<Object[]> totalVendasPorRestaurante = pedidoRepository.calcularTotalVendasPorRestaurante();
        System.out.println("Total de Vendas por Restaurante: " + totalVendasPorRestaurante.size());
        totalVendasPorRestaurante.forEach(obj -> System.out.println("  Restaurante: " + obj[0] + ", Total: " + obj[1]));

        List<Pedido> pedidosAcimaDe = pedidoRepository.buscarPedidosComValorAcimaDe(new BigDecimal("50.00"));
        System.out.println("Pedidos com valor acima de 50.00: " + pedidosAcimaDe.size());

        List<Pedido> relatorioPedidos = pedidoRepository.relatorioPedidosPorPeriodoEStatus(inicioPeriodo, fimPeriodo, StatusPedido.PENDENTE);
        System.out.println("Relatório de Pedidos (Pendentes nos últimos 10 dias): " + relatorioPedidos.size());
    }
}