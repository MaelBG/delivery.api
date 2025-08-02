package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.dto.RestauranteDTO;
import com.deliverytech.delivery_api.entity.Restaurante;
import com.deliverytech.delivery_api.exception.BusinessException;
import com.deliverytech.delivery_api.repository.RestauranteRepository;
import com.deliverytech.delivery_api.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários Finais do RestauranteService")
class RestauranteServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @InjectMocks
    private RestauranteService restauranteService;

    private Restaurante restaurante;
    private Pageable pageable;

    @BeforeEach
    void setup() {
        restaurante = new Restaurante();
        restaurante.setNome("Teste");
        restaurante.setTaxaEntrega(BigDecimal.TEN);
        pageable = PageRequest.of(0, 10);
    }
    
    // --- TESTES PARA OS MÉTODOS COM BAIXA COBERTURA ---

    @Test
    @DisplayName("Deve listar restaurantes filtrando por categoria e status ativo")
    void deveListarRestaurantesComCategoriaEAtivo() {
        Page<Restaurante> page = new PageImpl<>(Collections.singletonList(restaurante));
        when(restauranteRepository.findByCategoriaAndAtivoTrue(anyString(), any(Pageable.class))).thenReturn(page);
        
        restauranteService.listarRestaurantes("Italiana", true, pageable);
        
        verify(restauranteRepository).findByCategoriaAndAtivoTrue("Italiana", pageable);
    }
    
    @Test
    @DisplayName("Deve listar restaurantes filtrando apenas por status inativo")
    void deveListarRestaurantesApenasInativos() {
        Page<Restaurante> page = new PageImpl<>(Collections.singletonList(restaurante));
        when(restauranteRepository.findByAtivoFalse(any(Pageable.class))).thenReturn(page);

        restauranteService.listarRestaurantes(null, false, pageable);

        verify(restauranteRepository).findByAtivoFalse(pageable);
    }

    @Test
    @DisplayName("Deve listar todos os restaurantes quando nenhum filtro é aplicado")
    void deveListarTodosRestaurantesSemFiltro() {
        Page<Restaurante> page = new PageImpl<>(Collections.singletonList(restaurante));
        when(restauranteRepository.findAll(any(Pageable.class))).thenReturn(page);

        restauranteService.listarRestaurantes(null, null, pageable);

        verify(restauranteRepository).findAll(pageable);
    }
    
    @Test
    @DisplayName("Deve lançar exceção ao validar restaurante com nome nulo")
    void deveLancarExcecaoQuandoNomeNulo() {
        restaurante.setNome(null);
        assertThrows(BusinessException.class, () -> {
            // Este teste chama um método que internamente usa a validação
            restauranteService.atualizarRestaurante(1L, new RestauranteDTO()); 
        });
    }

    @Test
    @DisplayName("Deve retornar true se o usuário logado for dono do restaurante")
    void deveRetornarTrueParaIsOwner() {
        // Mocka a chamada estática para SecurityUtils
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(1L);
            
            assertTrue(restauranteService.isOwner(1L));
        }
    }
    
    @Test
    @DisplayName("Deve retornar false se o usuário logado não for dono do restaurante")
    void deveRetornarFalseParaIsNotOwner() {
        try (MockedStatic<SecurityUtils> mocked = Mockito.mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentRestauranteld).thenReturn(2L);
            
            assertFalse(restauranteService.isOwner(1L));
        }
    }
}