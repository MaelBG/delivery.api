# Delivery Tech API

API REST completa para um sistema de delivery, desenvolvida com as tecnologias mais recentes do ecossistema Java e Spring. O projeto serve como back-end para gerenciar restaurantes, produtos, clientes, pedidos e autenticação de usuários, com um robusto sistema de monitoramento e observabilidade.

## ✨ Features

  * **Autenticação e Autorização:** Sistema completo de login e registro com JWT e controle de acesso por perfis (`CLIENTE`, `RESTAURANTE`, `ADMIN`).
  * **Gerenciamento de Restaurantes:** CRUD completo para restaurantes, com busca por categoria e status.
  * **Gerenciamento de Produtos:** CRUD completo para produtos, associados a restaurantes.
  * **Gerenciamento de Pedidos:** Fluxo completo de criação, consulta, atualização de status e cancelamento de pedidos.
  * **Validação de Dados:** Validações robustas na camada de DTOs para garantir a integridade dos dados.
  * **Documentação Interativa:** API 100% documentada com Swagger/OpenAPI 3, permitindo a visualização e teste de todos os endpoints.
  * **Monitoramento e Observabilidade:**
      * **Health Checks:** Endpoints do Spring Boot Actuator para verificar a saúde da aplicação e suas dependências (como o banco de dados).
      * **Métricas em Tempo Real:** Coleta de métricas de negócio e de performance com Micrometer, prontas para serem consumidas pelo Prometheus.
      * **Logging Estruturado:** Logs em formato JSON com **Correlation ID** para rastreabilidade completa das requisições.
      * **Alertas Proativos:** Sistema de alertas que monitora métricas críticas (taxa de erro, tempo de resposta) e gera logs de aviso.

## 🚀 Tecnologias Utilizadas

  * **Linguagem:** Java 21 LTS
  * **Framework:** Spring Boot 3.3.1
      * **Spring Web:** Para a construção de endpoints RESTful.
      * **Spring Data JPA:** Para persistência de dados e comunicação com o banco.
      * **Spring Security:** Para controle de autenticação e autorização via JWT.
      * **Spring Boot Actuator:** Para monitoramento e health checks.
      * **Spring Boot Starter Validation:** Para validação dos dados de entrada.
  * **Banco de Dados:** H2 Database (em memória, ideal para desenvolvimento).
  * **Documentação:** SpringDoc (Swagger/OpenAPI 3).
  * **Autenticação:** JSON Web Tokens (JWT).
  * **Métricas:** Micrometer com Registry para Prometheus.
  * **Build Tool:** Maven.
  * **Containerização:** Docker & Docker Compose.
  * **Utilitários:** Lombok.

## 📋 Pré-requisitos

  * JDK 21 ou superior.
  * Maven 3.9+ (ou utilize o Maven Wrapper incluído).
  * Docker e Docker Compose (para rodar o Prometheus).

## 🏃 Como Executar a Aplicação

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/seu-usuario/delivery-api.git
    cd delivery-api
    ```

2.  **Execute a aplicação com o Maven Wrapper:**

      * No Windows:
        ```bash
        mvnw.cmd spring-boot:run
        ```
      * No Linux ou macOS:
        ```bash
        ./mvnw spring-boot:run
        ```

    A aplicação estará disponível em `http://localhost:8080`.

## 📖 Documentação e Uso da API (Swagger)

A documentação interativa da API está disponível via Swagger UI.

1.  **Acesse a documentação:**

      * Com a aplicação rodando, acesse: [http://localhost:8080/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/swagger-ui.html)

2.  **Como Testar Endpoints Protegidos:**

      * Na interface do Swagger, execute o endpoint `POST /api/auth/login` com as credenciais de um usuário (ex: `joao.cliente@email.com` / `123456`).
      * Copie o token JWT retornado.
      * Clique no botão **"Authorize"** no topo da página e cole o token no formato `Bearer <SEU_TOKEN>`.
      * Agora você pode testar todos os endpoints protegidos.

## 🔬 Monitoramento e Observabilidade

Este projeto implementa os 3 pilares da observabilidade: Métricas, Logs e Traces (via Correlation ID).

#### 1\. Endpoints do Actuator

Com a aplicação rodando, você pode acessar os seguintes endpoints para monitoramento:

  * **Saúde da Aplicação:** [http://localhost:8080/actuator/health](https://www.google.com/search?q=http://localhost:8080/actuator/health)
  * **Métricas para Prometheus:** [http://localhost:8080/actuator/prometheus](https://www.google.com/search?q=http://localhost:8080/actuator/prometheus)
  * **Informações da Aplicação:** [http://localhost:8080/actuator/info](https://www.google.com/search?q=http://localhost:8080/actuator/info)

#### 2\. Visualizando Métricas com Prometheus

Para visualizar as métricas em tempo real:

1.  **Inicie o Prometheus:** Na raiz do projeto, execute o comando:

    ```bash
    docker compose up
    ```

2.  **Acesse o Prometheus:** Abra seu navegador e acesse [http://localhost:9090](https://www.google.com/search?q=http://localhost:9090).

3.  **Consulte as Métricas:** No campo "Expression", você pode consultar as métricas customizadas, como:

      * `delivery_pedidos_total_total`
      * `delivery_pedidos_sucesso_total`
      * `delivery_pedidos_erro_total`

#### 3\. Logs Estruturados e Auditoria

  * **Logs de Aplicação:** Os logs operacionais são gerados em formato JSON no arquivo `logs/delivery-api-json.log`. Cada log contém um `correlationId` que permite rastrear uma requisição do início ao fim.
  * **Logs de Auditoria:** Ações críticas, como tentativas de login, são registradas no arquivo `logs/delivery-api-audit.log`.

## 🧪 Testes Automatizados

O projeto possui uma suíte de testes unitários e de integração para garantir a qualidade do código, com cobertura mínima de 80% validada pelo JaCoCo.

  * **Executar todos os testes e verificar a cobertura:**
    ```bash
    # No Windows
    mvnw.cmd clean verify

    # No Linux ou macOS
    ./mvnw clean verify
    ```
  * O relatório de cobertura interativo estará disponível em `target/site/jacoco/index.html`.

-----

## Otimização de Performance com Cache

Para melhorar a performance e reduzir a carga no banco de dados, foi implementado um sistema de cache para a entidade `Cliente`.

### Funcionalidades

- **Cache de Listagem**: A rota `GET /clientes` tem seu resultado armazenado em cache após a primeira chamada. Requisições subsequentes para a mesma rota são atendidas de forma quase instantânea, pois os dados são recuperados da memória.
- **Invalidação Automática**: O cache é automaticamente limpo (`evict`) sempre que um cliente é criado (`POST`), atualizado (`PUT`) ou inativado (`DELETE`). Isso garante a consistência dos dados e evita que informações desatualizadas sejam servidas.
- **Invalidação Manual**: Foi criado um endpoint específico, `GET /clientes/cache/limpar`, para permitir a limpeza manual do cache, útil para cenários de manutenção ou depuração.

### Implementação Técnica

A funcionalidade foi implementada utilizando o suporte nativo do Spring Boot:

1.  **`@EnableCaching`**: Adicionada à classe `Application.java` para ativar o framework de cache.
2.  **`CacheConfig.java`**: Foi criada uma classe de configuração para definir explicitamente um `CacheManager` do tipo `ConcurrentMapCacheManager`, registrando o cache chamado `"clientes"`. Isso garante que o Spring saiba onde armazenar os dados cacheados.
3.  **Anotações de Cache**:
    - `@Cacheable("clientes")`: Utilizada no método `listarAtivos()` do `ClienteService`.
    - `@CacheEvict(value = "clientes", allEntries = true)`: Utilizada nos métodos `cadastrar()`, `atualizar()` e `inativar()` do `ClienteService`, e também no endpoint de limpeza manual no `ClienteController`.

### Como Testar o Cache

1.  Inicie a aplicação.
2.  Faça uma requisição **`GET /clientes`**. Observe que a primeira resposta terá uma latência maior (simulada em 3 segundos).
3.  Faça a **mesma requisição** novamente. A resposta será quase instantânea.
4.  Faça uma requisição **`GET /clientes/cache/limpar`**. Você receberá uma resposta `204 No Content`.
5.  Repita o passo 2. A latência alta retornará, provando que o cache foi limpo com sucesso.

---

Desenvolvido com ❤️ por Ismael Barbosa Galdino Filho.