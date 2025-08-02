package com.deliverytech.delivery_api.config;

import com.deliverytech.delivery_api.entity.*;
import com.deliverytech.delivery_api.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
@Profile("!test") // Garante que este loader não execute durante os testes
public class DataLoader implements CommandLineRunner {

    @Autowired private ClienteRepository clienteRepository;
    @Autowired private RestauranteRepository restauranteRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository; // <-- ESSENCIAL

    // Senha pré-gerada para "123456"
    private final String HASHED_PASSWORD = "$2a$10$novAfxxa3Gy9KEfQ0uvhAOTaaunN/Vfak/R2dMs5BM9kkm/K6rDrq";

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("=== INICIANDO CARGA DE DADOS DE TESTE COM DATALOADER ===");

        // 1. Limpar dados existentes na ordem correta
        pedidoRepository.deleteAll();
        produtoRepository.deleteAll();
        usuarioRepository.deleteAll(); // Limpa os usuários antes
        clienteRepository.deleteAll();
        restauranteRepository.deleteAll();

        // 2. Inserir dados de teste
        inserirClientes();
        inserirRestaurantes();
        inserirProdutos();
        inserirPedidosEItens();
        inserirUsuarios(); // <-- ESSENCIAL

        System.out.println("=== CARGA DE DADOS CONCLUÍDA ===");
    }

    private void inserirClientes() {
        System.out.println("--- Inserindo Clientes ---");
        Cliente c1 = new Cliente(); c1.setNome("João Silva"); c1.setEmail("joao@email.com"); c1.setTelefone("(11) 99999-1111"); c1.setEndereco("Rua A, 123 - São Paulo/SP"); c1.setAtivo(true);
        Cliente c2 = new Cliente(); c2.setNome("Maria Santos"); c2.setEmail("maria@email.com"); c2.setTelefone("(11) 99999-2222"); c2.setEndereco("Rua B, 456 - São Paulo/SP"); c2.setAtivo(true);
        Cliente c3 = new Cliente(); c3.setNome("Pedro Oliveira"); c3.setEmail("pedro@email.com"); c3.setTelefone("(11) 99999-3333"); c3.setEndereco("Rua C, 789 - São Paulo/SP"); c3.setAtivo(true);
        clienteRepository.saveAll(Arrays.asList(c1, c2, c3));
        System.out.println("3 clientes inseridos.");
    }

    private void inserirRestaurantes() {
        System.out.println("--- Inserindo Restaurantes ---");
        Restaurante r1 = new Restaurante(); r1.setNome("Pizzaria Bella"); r1.setCategoria("Italiana"); r1.setEndereco("Av. Paulista, 1000"); r1.setTelefone("(11) 3333-1111"); r1.setTaxaEntrega(new BigDecimal("5.00")); r1.setAvaliacao(new BigDecimal("4.5")); r1.setAtivo(true);
        Restaurante r2 = new Restaurante(); r2.setNome("Burger House"); r2.setCategoria("Hamburgueria"); r2.setEndereco("Rua Augusta, 500"); r2.setTelefone("(11) 3333-2222"); r2.setTaxaEntrega(new BigDecimal("3.50")); r2.setAvaliacao(new BigDecimal("4.2")); r2.setAtivo(true);
        Restaurante r3 = new Restaurante(); r3.setNome("Sushi Master"); r3.setCategoria("Japonesa"); r3.setEndereco("Rua Liberdade, 200"); r3.setTelefone("(11) 3333-3333"); r3.setTaxaEntrega(new BigDecimal("8.00")); r3.setAvaliacao(new BigDecimal("4.8")); r3.setAtivo(true);
        restauranteRepository.saveAll(Arrays.asList(r1, r2, r3));
        System.out.println("3 restaurantes inseridos.");
    }

    private void inserirProdutos() {
        System.out.println("--- Inserindo Produtos ---");
        Restaurante r1 = restauranteRepository.findByNome("Pizzaria Bella").get();
        Restaurante r2 = restauranteRepository.findByNome("Burger House").get();
        Restaurante r3 = restauranteRepository.findByNome("Sushi Master").get();
        Produto p1 = new Produto(); p1.setNome("Pizza Margherita"); p1.setPreco(new BigDecimal("35.90")); p1.setCategoria("Pizza"); p1.setDisponivel(true); p1.setRestaurante(r1);
        Produto p2 = new Produto(); p2.setNome("Pizza Calabresa"); p2.setPreco(new BigDecimal("38.90")); p2.setCategoria("Pizza"); p2.setDisponivel(true); p2.setRestaurante(r1);
        Produto p4 = new Produto(); p4.setNome("X-Burger"); p4.setPreco(new BigDecimal("18.90")); p4.setCategoria("Hambúrguer"); p4.setDisponivel(true); p4.setRestaurante(r2);
        Produto p6 = new Produto(); p6.setNome("Batata Frita"); p6.setPreco(new BigDecimal("12.90")); p6.setCategoria("Acompanhamento"); p6.setDisponivel(true); p6.setRestaurante(r2);
        Produto p7 = new Produto(); p7.setNome("Combo Sashimi"); p7.setPreco(new BigDecimal("45.90")); p7.setCategoria("Sashimi"); p7.setDisponivel(true); p7.setRestaurante(r3);
        produtoRepository.saveAll(Arrays.asList(p1, p2, p4, p6, p7));
        System.out.println("5 produtos inseridos.");
    }

    private void inserirPedidosEItens() {
        System.out.println("--- Inserindo Pedidos ---");
        Cliente c1 = clienteRepository.findByEmail("joao@email.com").get();
        Restaurante r1 = restauranteRepository.findByNome("Pizzaria Bella").get();
        Produto prod1 = produtoRepository.findById(1L).get();
        Pedido pedido1 = new Pedido();
        pedido1.setNumeroPedido("PED1234567890");
        pedido1.setStatus(StatusPedido.PENDENTE);
        pedido1.setCliente(c1);
        pedido1.setRestaurante(r1);
        pedido1.setEnderecoEntrega(c1.getEndereco());
        ItemPedido item1 = new ItemPedido(); item1.setProduto(prod1); item1.setQuantidade(1); item1.setPrecoUnitario(prod1.getPreco()); item1.calcularSubtotal();
        pedido1.adicionarItem(item1);
        pedido1.calcularTotais();
        pedidoRepository.save(pedido1);
        System.out.println("1 pedido inserido.");
    }
    
    private void inserirUsuarios() {
        System.out.println("--- Inserindo Usuários ---");
        Restaurante r1 = restauranteRepository.findByNome("Pizzaria Bella").get();
        Usuario admin = new Usuario("admin@delivery.com", HASHED_PASSWORD, "Admin Sistema", Role.ADMIN);
        Usuario cliente = new Usuario("joao.cliente@email.com", HASHED_PASSWORD, "João Cliente", Role.CLIENTE);
        Usuario restauranteUser = new Usuario("pizza@palace.com", HASHED_PASSWORD, "Pizza Palace", Role.RESTAURANTE);
        restauranteUser.setRestauranteld(r1.getId());
        usuarioRepository.saveAll(Arrays.asList(admin, cliente, restauranteUser));
        System.out.println("3 usuários inseridos.");
    }
}