package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Produto;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.ProdutoRepository;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.dto.ProdutoDTO;
import com.deliverytech.delivery_api.dto.ProdutoResponseDTO;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.security.SecurityUtils; // Import adicionado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private RestauranteRepository restauranteRepository;
    
    // ... (métodos de mapeamento toResponseDTO e toEntity)

    private ProdutoResponseDTO toResponseDTO(Produto produto) {
        if (produto == null) return null;
        return new ProdutoResponseDTO(
                produto.getId(), produto.getNome(), produto.getDescricao(), produto.getPreco(),
                produto.getCategoria(), produto.isDisponivel(),
                produto.getRestaurante() != null ? produto.getRestaurante().getId() : null
        );
    }

    private Produto toEntity(ProdutoDTO dto) {
        if (dto == null) return null;
        Produto produto = new Produto();
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        produto.setDisponivel(true);
        return produto;
    }

    // ... (outros métodos do serviço como cadastrar, buscar, etc.)
    public ProdutoResponseDTO cadastrarProduto(ProdutoDTO dto, Long restauranteId) {
        Restaurante restaurante = restauranteRepository.findById(restauranteId)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId));
        validarDadosProduto(toEntity(dto));
        Produto produto = toEntity(dto);
        produto.setRestaurante(restaurante);
        produto.setDisponivel(true);
        Produto produtoSalvo = produtoRepository.save(produto);
        return toResponseDTO(produtoSalvo);
    }

    @Transactional(readOnly = true)
    public ProdutoResponseDTO buscarProdutoPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        return toResponseDTO(produto);
    }

    public ProdutoResponseDTO atualizarProduto(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        validarDadosProduto(toEntity(dto));
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setPreco(dto.getPreco());
        produto.setCategoria(dto.getCategoria());
        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    public void removerProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        produtoRepository.deleteById(id);
    }

    public ProdutoResponseDTO alterarDisponibilidadeProduto(Long id) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Produto", id));
        produto.setDisponivel(!produto.isDisponivel());
        Produto produtoAtualizado = produtoRepository.save(produto);
        return toResponseDTO(produtoAtualizado);
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorRestaurante(Long restauranteId, Boolean disponivel) {
        restauranteRepository.findById(restauranteId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurante", restauranteId));
        List<Produto> produtos;
        if (disponivel != null) {
            if (disponivel) produtos = produtoRepository.findByRestauranteIdAndDisponivelTrue(restauranteId);
            else produtos = produtoRepository.findByRestauranteIdAndDisponivelFalse(restauranteId);
        } else {
            produtos = produtoRepository.findByRestauranteId(restauranteId);
        }
        return produtos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorCategoria(String categoria) {
        List<Produto> produtos = produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
        return produtos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponseDTO> buscarProdutosPorNome(String nome) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndDisponivelTrue(nome);
        return produtos.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private void validarDadosProduto(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome do produto é obrigatório", "VALIDATION_ERROR");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Preço do produto deve ser maior que zero", "VALIDATION_ERROR");
        }
    }
    
    // --- NOVO MÉTODO PARA AUTORIZAÇÃO ---
    public boolean isOwner(Long produtoId) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto", produtoId));
        
        Long usuarioRestauranteId = SecurityUtils.getCurrentRestauranteld();
        if (usuarioRestauranteId == null || produto.getRestaurante() == null) {
            return false;
        }
        return usuarioRestauranteId.equals(produto.getRestaurante().getId());
    }
}