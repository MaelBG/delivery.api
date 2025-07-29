package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.exception.EntityNotFoundException; // Importar EntityNotFoundException
import com.deliverytech.delivery_api.exception.BusinessException; // Importar BusinessException (para validações mais genéricas)

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    // --- Métodos de Mapeamento (Helper Methods) ---

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) {
            return null;
        }
        return new ProdutoResponseDTO(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getCategoria(),
                produto.isDisponivel(),
                produto.getRestaurante() != null ? produto.getRestaurante().getId() : null
        );
    }

    private Produto toEntity(ProdutoDTO dto) {
        if (dto == null) {
            return null;
        }
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponivel(true); // Padrão ao cadastrar
        return produto;
    }

    // --- Métodos do Serviço (Atualizados para usar DTOs) ---

    /**
     * Cadastrar novo produto
     */
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto, Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId)); // Lança EntityNotFoundException

        validarDadosProduto(toEntity(dto));

        Produto produto = toEntity(dto);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        Produto produtoSalvo = produtoRepository.save(produto);
        return toResponseDTO(produtoSalvo);
    }

    /**
     * Buscar produto por ID
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id)); // Lança EntityNotFoundException
        return toResponseDTO(produto);
    }

    /**
     * Atualizar produto
     */
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id)); // Lança EntityNotFoundException

        validarDadosProduto(toEntity(dto));

        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());

        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    /**
     * Remover produto
     */
    public void removerProduto(Long id) {
        Produto produto = produtoRepository.findById(id) // Busca o produto para verificar existência
            .orElseThrow(() -> new EntityNotFoundException("Produto", id)); // Lança EntityNotFoundException

        // TODO: Adicionar lógica para verificar se o produto tem pedidos associados antes de remover (ATIVIDADE 4 - Cenários de Conflito)
        // Isso exigiria uma verificação no ItemPedidoRepository ou PedidoRepository.
        // Exemplo: if (itemPedidoRepository.existsByProdutoId(id)) { throw new ConflictException("Produto possui pedidos associados"); }

        produtoRepository.deleteById(id);
    }

    /**
     * Alterar disponibilidade de produto
     */
    public ProdutoResponseDTO alterarDisponibilidadeProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id)); // Lança EntityNotFoundException

        produto.setDisponivel(!produto.isDisponivel());
        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    /**
     * Listar produtos de um restaurante (para RestauranteController e ProdutoController)
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId)); // Lança EntityNotFoundException

        List<Produto> produtos;
        if (disponivel != null) {
            if (disponivel) {
                produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
            } else {
                produtos = produtoRepository.findByRestauranteIdAndDisponivelFalse(restauranteId);
            }
        } else {
            produtos = produtoRepository.findByRestauranteId(restauranteId);
        }

        return produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar produtos por categoria
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
        return produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar produtos por nome
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
        return produtos.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // --- Métodos Privados de Validação (Inalterados, agora lançando BusinessException) ---

    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do produto é obrigatório", "VALIDATION_ERROR");
        }

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço do produto deve ser maior que zero", "VALIDATION_ERROR");
        }
    }
}