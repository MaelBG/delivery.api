package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO;
import com.deliverytech.delivery_api.exception.ConflictException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.security.SecurityUtils; // Import adicionado
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    // ... (métodos de mapeamento toResponseDTO e toEntity)

    private RestauranteResponseDTO toResponseDTO(Restaurante restaurante) {
        if (restaurante == null) return null;
        return new RestauranteResponseDTO(
                restaurante.getId(), restaurante.getNome(), restaurante.getCategoria(),
                restaurante.getEndereco(), restaurante.getTelefone(), restaurante.getTaxaEntrega(),
                restaurante.getAvaliacao(), restaurante.isAtivo(), restaurante.getTempoEntrega(),
                restaurante.getHorarioFuncionamento()
        );
    }

    private Restaurante toEntity(RestauranteDTO dto) {
        if (dto == null) return null;
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        restaurante.setAvaliacao(BigDecimal.ZERO);
        restaurante.setAtivo(true);
        restaurante.setTempoEntrega(dto.getTempoEntrega());
        restaurante.setHorarioFuncionamento(dto.getHorarioFuncionamento());
        return restaurante;
    }
    
    // ... (outros métodos do serviço como cadastrar, listar, etc.)

    public RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto) {
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new ConflictException("Restaurante já cadastrado com este nome: " + dto.getNome(), "nome", dto.getNome());
        }
        Restaurante restaurante = toEntity(dto);
        restaurante.setAtivo(true);
        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteSalvo);
    }

    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> listarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        Page<Restaurante> restaurantesPage;
        if (categoria != null && ativo != null) {
            restaurantesPage = restauranteRepository.findByCategoriaAndAtivoTrue(categoria, pageable);
        } else if (categoria != null) {
            restaurantesPage = restauranteRepository.findByCategoriaAndAtivoTrue(categoria, pageable);
        } else if (ativo != null) {
            if (ativo) restaurantesPage = restauranteRepository.findByAtivoTrue(pageable);
            else restaurantesPage = restauranteRepository.findByAtivoFalse(pageable);
        } else {
            restaurantesPage = restauranteRepository.findAll(pageable);
        }
        return restaurantesPage.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        return toResponseDTO(restaurante);
    }

    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        if (!restaurante.getNome().equals(dto.getNome()) && restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new ConflictException("Nome já cadastrado para outro restaurante: " + dto.getNome(), "nome", dto.getNome());
        }
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        restaurante.setTempoEntrega(dto.getTempoEntrega());
        restaurante.setHorarioFuncionamento(dto.getHorarioFuncionamento());
        validarDadosRestaurante(restaurante);
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteAtualizado);
    }

    public RestauranteResponseDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        restaurante.setAtivo(!restaurante.isAtivo());
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteAtualizado);
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
        return restaurantes.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Restaurante", id));
        return restaurante.getTaxaEntrega();
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Integer raio) {
        List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();
        return restaurantes.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório", "VALIDATION_ERROR");
        }
        if (restaurante.getTaxaEntrega() != null && restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Taxa de entrega não pode ser negativa", "VALIDATION_ERROR");
        }
    }

    // --- NOVO MÉTODO PARA AUTORIZAÇÃO ---
    public boolean isOwner(Long restauranteId) {
        Long usuarioRestauranteId = SecurityUtils.getCurrentRestauranteld();
        if (usuarioRestauranteId == null) {
            return false;
        }
        return usuarioRestauranteId.equals(restauranteId);
    }
}