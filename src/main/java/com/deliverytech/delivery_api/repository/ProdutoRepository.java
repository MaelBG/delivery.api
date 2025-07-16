package com.deliverytech.delivery_api.repository;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.entity.Produto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
// Buscar produtos por restaurante
List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);
// Buscar produtos por restaurante ID (e disponíveis)
List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);
// Buscar por categoria (e disponíveis)
List<Produto> findByCategoriaAndDisponivelTrue(String categoria);
// Buscar apenas produtos disponíveis
List<Produto> findByDisponivelTrue();
// Buscar por nome contendo
List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);
// Buscar por faixa de preço
    List<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal
precoMax);

    // Buscar produtos mais baratos que um valor (e disponíveis)
    List<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco);

    // Ordenar por preço
    List<Produto> findByDisponivelTrueOrderByPrecoAsc();
    List<Produto> findByDisponivelTrueOrderByPrecoDesc();

    // Query customizada - produtos mais vendidos (já existia e estava conforme roteiro)
    @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
           "FROM produto p " +
           "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
           "GROUP BY p.id, p.nome " +
           "ORDER BY quantidade_vendida DESC " +
           "LIMIT 5", nativeQuery = true)
    List<Object[]> findProdutosMaisVendidos();

    // Buscar por restaurante e categoria
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId " +
           "AND p.categoria = :categoria AND p.disponivel = true")
    List<Produto> findByRestauranteAndCategoria(@Param("restauranteId") Long restauranteId,
                                               @Param("categoria") String categoria);

    // Contar produtos por restaurante
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
    Long countByRestauranteId(@Param("restauranteId") Long restauranteId);
}