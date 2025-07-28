package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.dto.RestauranteDTO; // Importe o DTO de requisição
import com.deliverytech.delivery_api.dto.RestauranteResponseDTO; // Importe o DTO de resposta
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page; // Para paginação
import org.springframework.data.domain.Pageable; // Para paginação
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Para mapear listas

@Service
@Transactional
public class RestauranteService {

    @Autowired
    private RestauranteRepository restauranteRepository;

    // --- Métodos de Mapeamento (Helper Methods) ---

    // Converte Restaurante (Entidade) para RestauranteResponseDTO
    private RestauranteResponseDTO toResponseDTO(Restaurante restaurante) {
        if (restaurante == null) {
            return null;
        }
        // Assumindo que tempoEntrega e horarioFuncionamento foram persistidos/preenchidos
        return new RestauranteResponseDTO(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getCategoria(),
                restaurante.getEndereco(),
                restaurante.getTelefone(),
                restaurante.getTaxaEntrega(),
                restaurante.getAvaliacao(),
                restaurante.isAtivo(),
                // Estes campos não existem na sua entidade Restaurante, então retornaremos null ou um default
                null, // tempoEntrega - você precisaria adicionar este campo à entidade
                null  // horarioFuncionamento - você precisaria adicionar este campo à entidade
        );
    }

    // Converte RestauranteDTO para Restaurante (Entidade)
    private Restaurante toEntity(RestauranteDTO dto) {
        if (dto == null) {
            return null;
        }
        Restaurante restaurante = new Restaurante();
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());
        restaurante.setAvaliacao(BigDecimal.ZERO); // Valor padrão ou defina conforme a lógica de negócio
        restaurante.setAtivo(true); // Padrão ao cadastrar

        // Adicionar estes campos à entidade Restaurante se quiser persistí-los
        // restaurante.setTempoEntrega(dto.getTempoEntrega());
        // restaurante.setHorarioFuncionamento(dto.getHorarioFuncionamento());

        return restaurante;
    }

    // --- Métodos do Serviço (Atualizados para usar DTOs e paginacao) ---

    /**
     * Cadastrar novo restaurante
     */
    public RestauranteResponseDTO cadastrarRestaurante(RestauranteDTO dto) {
        // Validar nome único
        if (restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new IllegalArgumentException("Restaurante já cadastrado: " + dto.getNome());
        }

        // Validações de negócio do DTO já feitas pelo @Valid no Controller
        // Mas podemos adicionar mais aqui se necessário, como validarTaxaEntrega (já existe no método privado)

        Restaurante restaurante = toEntity(dto); // Converte DTO para Entidade
        restaurante.setAtivo(true); // Definir como ativo por padrão

        Restaurante restauranteSalvo = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteSalvo); // Converte Entidade salva para DTO de resposta
    }

    /**
     * Listar restaurantes com filtros e paginação
     */
    @Transactional(readOnly = true)
    public Page<RestauranteResponseDTO> listarRestaurantes(String categoria, Boolean ativo, Pageable pageable) {
        Page<Restaurante> restaurantesPage;

        if (categoria != null && ativo != null) {
            restaurantesPage = restauranteRepository.findByCategoriaAndAtivoTrue(categoria, pageable); // Assuming findByCategoriaAndAtivoTrue can accept Pageable
        } else if (categoria != null) {
            restaurantesPage = restauranteRepository.findByCategoriaAndAtivoTrue(categoria, pageable); // You might need a specific findByCategoria that takes Pageable
        } else if (ativo != null) {
            if (ativo) {
                restaurantesPage = restauranteRepository.findByAtivoTrue(pageable); // Assuming findByAtivoTrue can accept Pageable
            } else {
                restaurantesPage = restauranteRepository.findByAtivoFalse(pageable); // Assuming findByAtivoFalse can accept Pageable
            }
        } else {
            // Se nenhum filtro, lista todos paginado
            restaurantesPage = restauranteRepository.findAll(pageable);
        }

        // Mapeia a Page de entidades para uma Page de DTOs
        return restaurantesPage.map(this::toResponseDTO);
    }

    /**
     * Buscar restaurante por ID
     */
    @Transactional(readOnly = true)
    public RestauranteResponseDTO buscarRestaurantePorId(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));
        return toResponseDTO(restaurante);
    }

    /**
     * Atualizar dados do restaurante
     */
    public RestauranteResponseDTO atualizarRestaurante(Long id, RestauranteDTO dto) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        // Verificar nome único (se mudou e está sendo usado por outro restaurante)
        if (!restaurante.getNome().equals(dto.getNome()) &&
            restauranteRepository.findByNome(dto.getNome()).isPresent()) {
            throw new IllegalArgumentException("Nome já cadastrado: " + dto.getNome());
        }

        // Atualizar campos da entidade com base no DTO
        restaurante.setNome(dto.getNome());
        restaurante.setCategoria(dto.getCategoria());
        restaurante.setEndereco(dto.getEndereco());
        restaurante.setTelefone(dto.getTelefone());
        restaurante.setTaxaEntrega(dto.getTaxaEntrega());

        // Adicionar estes campos à entidade Restaurante e atualizar aqui
        // restaurante.setTempoEntrega(dto.getTempoEntrega());
        // restaurante.setHorarioFuncionamento(dto.getHorarioFuncionamento());

        validarDadosRestaurante(restaurante); // Revalida dados de negócio

        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteAtualizado);
    }

    /**
     * Alterar status ativo/inativo do restaurante
     */
    public RestauranteResponseDTO alterarStatusRestaurante(Long id) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        restaurante.setAtivo(!restaurante.isAtivo()); // Inverte o status
        Restaurante restauranteAtualizado = restauranteRepository.save(restaurante);
        return toResponseDTO(restauranteAtualizado);
    }


    /**
     * Buscar restaurantes por categoria
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesPorCategoria(String categoria) {
        List<Restaurante> restaurantes = restauranteRepository.findByCategoriaAndAtivoTrue(categoria);
        return restaurantes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcular taxa de entrega
     * NOTA: A lógica para calcular taxa de entrega baseada em CEP é complexa e
     * geralmente envolve integração com APIs externas (Correios, ViaCEP, etc.).
     * Esta é uma implementação simplificada/placeholder.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTaxaEntrega(Long id, String cep) {
        Restaurante restaurante = restauranteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Restaurante não encontrado: " + id));

        // Lógica de cálculo simplificada: apenas retorna a taxa fixa do restaurante
        // Em um cenário real, você integraria com uma API de CEP/distância.
        return restaurante.getTaxaEntrega();
    }

    /**
     * Buscar restaurantes próximos
     * NOTA: A lógica para buscar restaurantes próximos a um CEP em um determinado raio
     * exige dados de geolocalização (latitude/longitude) para restaurantes e uma
     * base de dados de CEPs ou integração com serviços de mapas (Google Maps API, etc.).
     * Esta é uma implementação simplificada/placeholder.
     */
    @Transactional(readOnly = true)
    public List<RestauranteResponseDTO> buscarRestaurantesProximos(String cep, Integer raio) {
        // Implementação placeholder: retorna todos os restaurantes ativos, ignorando CEP e raio
        // Em um cenário real:
        // 1. Converter CEP para coordenadas geográficas.
        // 2. Buscar restaurantes com coordenadas e calcular distância.
        // 3. Filtrar por raio.
        List<Restaurante> restaurantes = restauranteRepository.findByAtivoTrue();
        return restaurantes.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    // --- Métodos Privados de Validação ---

    private void validarDadosRestaurante(Restaurante restaurante) {
        if (restaurante.getNome() == null || restaurante.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }

        if (restaurante.getTaxaEntrega() != null &&
            restaurante.getTaxaEntrega().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Taxa de entrega não pode ser negativa");
        }
    }
}