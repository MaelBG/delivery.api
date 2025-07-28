package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.dto.ProdutoDTO; // Importe o DTO de requisição
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO; // Importe o DTO de resposta

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para mapear listas

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;

    // --- Métodos de Mapeamento (Helper Methods) ---

    // Converte Produto (Entidade) para ProdutoResponseDTO
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

    // Converte ProdutoDTO para Produto (Entidade)
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
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        // Validações do DTO já feitas pelo @Valid no Controller
        validarDadosProduto(toEntity(dto)); // Usa a validação de negócio existente

        Produto produto = toEntity(dto); // Converte DTO para Entidade
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);

        Produto produtoSalvo = produtoRepository.save(produto);
        return toResponseDTO(produtoSalvo); // Converte Entidade salva para DTO de resposta
    }

    /**
     * Buscar produto por ID
     */
    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));
        return toResponseDTO(produto);
    }

    /**
     * Atualizar produto
     */
    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        // Validações do DTO já feitas pelo @Valid no Controller
        validarDadosProduto(toEntity(dto)); // Usa a validação de negócio existente

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
        if (!produtoRepository.existsById(id)) {
            throw new IllegalArgumentException("Produto não encontrado: " + id);
        }
        // TODO: Adicionar lógica para verificar se o produto tem pedidos associados antes de remover (ATIVIDADE 4 - Cenários de Conflito)
        // Isso exigiria uma verificação no ItemPedidoRepository ou PedidoRepository.
        produtoRepository.deleteById(id);
    }

    /**
     * Alterar disponibilidade de produto
     */
    public ProdutoResponseDTO alterarDisponibilidadeProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + id));

        produto.setDisponivel(!produto.isDisponivel()); // Inverte o status de disponibilidade
        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    /**
     * Listar produtos de um restaurante (para RestauranteController e ProdutoController)
     */
    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + restauranteId));

        List<Produto> produtos;
        if (disponivel != null) {
            // Se o filtro de disponibilidade for fornecido
            if (disponivel) {
                produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
            } else {
                produtos = produtoRepository.findByRestauranteIdAndDisponivelFalse(restauranteId); // Você pode precisar adicionar este método no ProdutoRepository
            }
        } else {
            // Se nenhum filtro de disponibilidade, retorna todos os produtos do restaurante
            produtos = produtoRepository.findByRestauranteId(restauranteId); // Você pode precisar adicionar este método no ProdutoRepository
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


    // --- Métodos Privados de Validação (Inalterados) ---

    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço deve ser maior que zero");
        }
    }
}