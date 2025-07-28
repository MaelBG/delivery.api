package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Restaurante;
import org.springframework.data.domain.Page; // Importe para suportar paginação
import org.springframework.data.domain.Pageable; // Importe para suportar paginação
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import com.deliverytech.delivery_api.repository.projections.RelatorioVendas;

@Repository
public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    // Buscar por nome
    Optional<Restaurante> findByNome(String nome);

    // Buscar restaurantes ativos
    List<Restaurante> findByAtivoTrue();
    // Buscar restaurantes ativos com paginação (Adicionado)
    Page<Restaurante> findByAtivoTrue(Pageable pageable);

    // Buscar restaurantes inativos com paginação (Adicionado)
    Page<Restaurante> findByAtivoFalse(Pageable pageable);

    // Buscar por categoria (e ativos)
    List<Restaurante> findByCategoriaAndAtivoTrue(String categoria);
    // Buscar por categoria (e ativos) com paginação (Adicionado)
    Page<Restaurante> findByCategoriaAndAtivoTrue(String categoria, Pageable pageable);


    // Buscar por nome contendo (case insensitive)
    List<Restaurante> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    // Buscar por avaliação mínima
    List<Restaurante> findByAvaliacaoGreaterThanEqualAndAtivoTrue(BigDecimal avaliacao);

    // Ordenar por avaliação (descendente)
    List<Restaurante> findByAtivoTrueOrderByAvaliacaoDesc();

    // Top 5 restaurantes por nome (ordem alfabética)
    List<Restaurante> findTop5ByOrderByNomeAsc();

    // Query customizada - restaurantes com produtos
    @Query("SELECT DISTINCT r FROM Restaurante r JOIN r.produtos p WHERE r.ativo = true")
    List<Restaurante> findRestaurantesComProdutos();

    // Buscar por faixa de taxa de entrega
    @Query("SELECT r FROM Restaurante r WHERE r.taxaEntrega BETWEEN :min AND :max AND r.ativo = true")
    List<Restaurante> findByTaxaEntregaBetween(@Param("min") BigDecimal min, @Param("max") BigDecimal max);

    // Buscar por taxa de entrega menor ou igual
    List<Restaurante> findByTaxaEntregaLessThanEqual(BigDecimal taxa);

    // Categorias disponíveis
    @Query("SELECT DISTINCT r.categoria FROM Restaurante r WHERE r.ativo = true ORDER BY r.categoria")
    List<String> findCategoriasDisponiveis();

    // Relatório de vendas por restaurante usando projeção
    @Query("SELECT r.nome as nomeRestaurante, " +
           "SUM(p.valorTotal) as totalVendas, " +
           "COUNT(p.id) as quantidePedidos " +
           "FROM Restaurante r " +
           "LEFT JOIN Pedido p ON r.id = p.restaurante.id " +
           "GROUP BY r.id, r.nome")
    List<RelatorioVendas> relatorioVendasPorRestaurante();
}