package com.deliverytech.delivery_api.service;

import com.deliverytech.delivery_api.entity.Cliente;
import com.deliverytech.delivery_api.repository.ClienteRepository;
import com.deliverytech.delivery_api.exception.ConflictException;
import com.deliverytech.delivery_api.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Cadastrar novo cliente
     */
    @CacheEvict(value = "clientes", allEntries = true) // Limpa o cache de clientes
    public Cliente cadastrar(Cliente cliente) {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new ConflictException("Email já cadastrado: " + cliente.getEmail(), "email", cliente.getEmail());
        }
        validarDadosCliente(cliente);
        cliente.setAtivo(true);
        return clienteRepository.save(cliente);
    }

    /**
     * Buscar cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    /**
     * Buscar cliente por email
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    /**
     * Listar todos os clientes ativos
     */
    @Transactional(readOnly = true)
    @Cacheable("clientes") // Armazena o resultado em cache
    public List<Cliente> listarAtivos() {
        simulateDelay(); // Simula demora para demonstrar o cache
        return clienteRepository.findByAtivoTrue();
    }

    /**
     * Atualizar dados do cliente
     */
    @CacheEvict(value = "clientes", allEntries = true) // Limpa o cache de clientes
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente cliente = buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));

        if (!cliente.getEmail().equals(clienteAtualizado.getEmail()) &&
            clienteRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new ConflictException("Email já cadastrado para outro cliente: " + clienteAtualizado.getEmail(), "email", clienteAtualizado.getEmail());
        }

        cliente.setNome(clienteAtualizado.getNome());
        cliente.setEmail(clienteAtualizado.getEmail());
        cliente.setTelefone(clienteAtualizado.getTelefone());
        cliente.setEndereco(clienteAtualizado.getEndereco());
        validarDadosCliente(cliente);
        return clienteRepository.save(cliente);
    }

    /**
     * Inativar cliente (soft delete)
     */
    @CacheEvict(value = "clientes", allEntries = true) // Limpa o cache de clientes
    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente", id));
        cliente.inativar();
        clienteRepository.save(cliente);
    }

    /**
     * Buscar clientes por nome
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Método para simular latência para testes de cache.
     */
    private void simulateDelay() {
        try {
            System.out.println("Simulando latência de 3 segundos... Acessando o banco de dados.");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Validações de negócio
     */
    private void validarDadosCliente(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (cliente.getNome().length() < 2) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
        }
    }
}