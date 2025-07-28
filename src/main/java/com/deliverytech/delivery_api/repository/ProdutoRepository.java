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
// Buscar produtos por restaurante (original)
List<Produto> findByRestauranteAndDisponivelTrue(Restaurante restaurante);
// Buscar produtos por restaurante ID (e disponíveis) (original)
List<Produto> findByRestauranteIdAndDisponivelTrue(Long restauranteId);
// Buscar por categoria (e disponíveis) (original)
List<Produto> findByCategoriaAndDisponivelTrue(String categoria);
// Buscar apenas produtos disponíveis (original)
List<Produto> findByDisponivelTrue();
// Buscar por nome contendo (original)
List<Produto> findByNomeContainingIgnoreCaseAndDisponivelTrue(String nome);
// Buscar por faixa de preço (original)
    List<Produto> findByPrecoBetweenAndDisponivelTrue(BigDecimal precoMin, BigDecimal
precoMax);

    // Buscar produtos mais baratos que um valor (e disponíveis) (original)
    List<Produto> findByPrecoLessThanEqualAndDisponivelTrue(BigDecimal preco);

    // Ordenar por preço (original)
    List<Produto> findByDisponivelTrueOrderByPrecoAsc();
    List<Produto> findByDisponivelTrueOrderByPrecoDesc();

    // Query customizada - produtos mais vendidos (já existia e estava conforme roteiro) (original)
    @Query(value = "SELECT p.nome, COUNT(ip.produto_id) as quantidade_vendida " +
           "FROM produto p " +
           "LEFT JOIN item_pedido ip ON p.id = ip.produto_id " +
           "GROUP BY p.id, p.nome " +
           "ORDER BY quantidade_vendida DESC " +
           "LIMIT 5", nativeQuery = true)
    List<Object[]> findProdutosMaisVendidos();

    // Buscar por restaurante e categoria (original)
    @Query("SELECT p FROM Produto p WHERE p.restaurante.id = :restauranteId " +
           "AND p.categoria = :categoria AND p.disponivel = true")
    List<Produto> findByRestauranteAndCategoria(@Param("restauranteId") Long restauranteId,
                                               @Param("categoria") String categoria);

    // Contar produtos por restaurante (original)
    @Query("SELECT COUNT(p) FROM Produto p WHERE p.restaurante.id = :restauranteId AND p.disponivel = true")
    Long countByRestauranteId(@Param("restauranteId") Long restauranteId);

    // --- NOVOS MÉTODOS ADICIONADOS PARA O PRODUTOSERVICE ---

    // Buscar todos os produtos de um restaurante por ID (Adicionado)
    List<Produto> findByRestauranteId(Long restauranteId);

    // Buscar produtos de um restaurante por ID e que não estão disponíveis (Adicionado)
    List<Produto> findByRestauranteIdAndDisponivelFalse(Long restauranteId);

    // --- FIM DOS NOVOS MÉTODOS ---
}