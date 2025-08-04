# Delivery Tech API

API REST completa para um sistema de delivery, desenvolvida com as tecnologias mais recentes do ecossistema Java e Spring. O projeto serve como back-end para gerenciar restaurantes, produtos, clientes, pedidos e autenticação de usuários, com um robusto sistema de monitoramento e observabilidade.

## ✨ Features

* **Autenticação e Autorização:** Sistema completo de login e registro com JWT e controle de acesso por perfis (`CLIENTE`, `RESTAURANTE`, `ADMIN`).
* **Gerenciamento de Restaurantes:** CRUD completo para restaurantes, com busca por categoria e status.
* **Gerenciamento de Produtos:** CRUD completo para produtos, associados a cada restaurante.
* **Gerenciamento de Pedidos:** Fluxo completo de criação, consulta, atualização de status e cancelamento de pedidos.
* **Caching:** Uso de cache com Redis para otimizar consultas frequentes, como a listagem de clientes.
* **Validação de Dados:** Validações robustas na camada de DTOs para garantir a integridade dos dados de entrada.
* **Documentação Interativa:** API 100% documentada com Swagger/OpenAPI 3, permitindo a visualização e teste de todos os endpoints.
* **Monitoramento e Observabilidade:**
    * **Health Checks:** Endpoints do Spring Boot Actuator para verificar a saúde da aplicação e suas dependências (banco de dados, Redis).
    * **Métricas em Tempo Real:** Coleta de métricas de negócio e de performance com Micrometer, prontas para serem consumidas pelo Prometheus.
    * **Logging Estruturado:** Logs em formato JSON com **Correlation ID** para rastreabilidade completa das requisições.

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java 21
* **Framework:** Spring Boot 3
* **Persistência:** Spring Data JPA com Hibernate
* **Banco de Dados:** MySQL
* **Cache:** Redis
* **Segurança:** Spring Security e JWT (JSON Web Tokens)
* **Documentação:** SpringDoc (Swagger/OpenAPI 3)
* **Observabilidade:** Spring Boot Actuator, Micrometer e Prometheus
* **Containerização:** Docker e Docker Compose
* **Build:** Maven

## 🐳 Como Executar o Projeto com Docker

A maneira mais simples de rodar este projeto é utilizando Docker e Docker Compose. Isso garante que todo o ambiente (API, Banco de Dados e Cache) suba de forma integrada.

### Pré-requisitos

* [Git](https://git-scm.com/)
* [Docker](https://www.docker.com/products/docker-desktop/)

### Passo a Passo

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/seu-usuario/delivery.api.git](https://github.com/seu-usuario/delivery.api.git)
    cd delivery.api
    ```

2.  **Construa as imagens Docker:**
    Este comando irá baixar as dependências, compilar o projeto e criar a imagem da sua API.
    ```bash
    docker-compose build
    ```

3.  **Inicie os contêineres:**
    Este comando irá iniciar sua aplicação, o banco de dados MySQL e o Redis.
    ```bash
    docker-compose up
    ```

Pronto! A aplicação estará rodando e pronta para receber requisições.

## 🚀 Acessando a Aplicação

* **API Base URL:** `http://localhost:8080`
* **Documentação Interativa (Swagger UI):** `http://localhost:8080/swagger-ui.html`
* **Endpoints de Métricas (Actuator):** `http://localhost:8080/actuator`
* **Métricas para Prometheus:** `http://localhost:8080/actuator/prometheus`

## 🧪 Testando o Cache

1.  Inicie a aplicação com Docker.
2.  Faça uma requisição **`GET /clientes`**. A primeira resposta terá uma latência maior (simulada em 3 segundos).
3.  Faça a **mesma requisição** novamente. A resposta será quase instantânea, pois os dados vieram do cache.
4.  Faça uma requisição **`GET /clientes/cache/limpar`** para limpar o cache.
5.  Repita o passo 2. A resposta voltará a ter a latência inicial, provando que o cache foi limpo.