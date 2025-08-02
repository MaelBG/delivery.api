Delivery Tech API
API REST completa para um sistema de delivery, desenvolvida com as tecnologias mais recentes do ecossistema Java e Spring. O projeto serve como back-end para gerenciar restaurantes, produtos, clientes, pedidos e autenticação de usuários.

🚀 Tecnologias Utilizadas
Este projeto foi construído utilizando um conjunto de tecnologias modernas para garantir performance, segurança e escalabilidade.


Linguagem: Java 21 LTS 


Framework: Spring Boot 3.3.1 


Módulos Spring:

Spring Web: Para a construção de endpoints RESTful.

Spring Data JPA: Para persistência de dados e comunicação com o banco.

Spring Security: Para controle de autenticação e autorização via JWT.

Spring Boot Starter Validation: Para validação dos dados de entrada (DTOs).


Banco de Dados: H2 Database (em memória, ideal para desenvolvimento e testes).


Documentação: SpringDoc (Swagger/OpenAPI 3) para documentação interativa da API.


Autenticação: JSON Web Tokens (JWT) para segurança de endpoints.


Build Tool: Maven.

Utilitários: Lombok para redução de código boilerplate.

✨ Features
Autenticação e Autorização: Sistema completo de login e registro com JWT e controle de acesso por perfis (CLIENTE, RESTAURANTE, ADMIN).

Gerenciamento de Restaurantes: CRUD completo para restaurantes, com busca por categoria e status.

Gerenciamento de Produtos: CRUD completo para produtos, associados a restaurantes.

Gerenciamento de Pedidos: Fluxo completo de criação, consulta, atualização de status e cancelamento de pedidos.

Validação de Dados: Validações robustas na camada de DTOs para garantir a integridade dos dados.

Documentação Interativa: API 100% documentada com Swagger UI, permitindo a visualização e teste de todos os endpoints.

📋 Pré-requisitos
Antes de começar, garanta que você tenha os seguintes softwares instalados em sua máquina:


JDK 21 ou superior.

Maven 3.9 ou superior (ou utilize o Maven Wrapper incluído).

🏃 Como Executar a Aplicação
Clone o repositório:

Bash

git clone https://github.com/seu-usuario/delivery-api.git
cd delivery-api
Execute a aplicação com o Maven Wrapper:

No Linux ou macOS:

Bash

./mvnw spring-boot:run
No Windows:

Bash

mvnw.cmd spring-boot:run
A aplicação estará disponível em http://localhost:8080.

📖 Documentação da API (Swagger)
A documentação completa da API foi gerada com SpringDoc e OpenAPI 3. Ela está disponível e pode ser testada interativamente através do Swagger UI.

Para acessar a documentação, execute a aplicação e acesse a seguinte URL:

http://localhost:8080/swagger-ui.html

A interface permite visualizar todos os endpoints, seus parâmetros, schemas de dados e testar as requisições diretamente no navegador.

Como Testar Endpoints Protegidos
Na interface do Swagger, vá até o endpoint POST /api/auth/login.

Utilize as credenciais de um usuário (ex: admin@delivery.com / 123456) para obter um token JWT.

Clique no botão "Authorize" no topo da página e cole o token precedido por Bearer  (ex: Bearer eyJhbGciOi...).

Agora você pode executar os endpoints protegidos que exigem autenticação.

🗺️ Endpoints Principais
A API está organizada nos seguintes grupos de endpoints:

/api/auth: Autenticação e registro de usuários.

/api/restaurantes: Gerenciamento de restaurantes.

/api/produtos: Gerenciamento de produtos.

/api/pedidos: Gerenciamento de pedidos.

/api/relatorios: Geração de relatórios.

📂 Estrutura do Projeto
O projeto segue uma estrutura padrão para aplicações Spring Boot, com os pacotes principais organizados da seguinte forma:

com.deliverytech.delivery_api
├── config/         # Classes de configuração (Security, Swagger, etc.)
├── controller/     # Controllers REST que expõem os endpoints da API
├── dto/            # Data Transfer Objects para requisições e respostas
├── entity/         # Entidades JPA que mapeiam as tabelas do banco
├── exception/      # Classes de exceções customizadas
├── repository/     # Interfaces do Spring Data JPA para acesso ao banco
├── security/       # Classes relacionadas à segurança com JWT
├── service/        # Lógica de negócio da aplicação
└── validation/     # Validadores customizados

---

## 🧪 Testes Automatizados

O projeto possui uma suíte completa de testes unitários e de integração para garantir a qualidade e a estabilidade do código. A cobertura de código é validada pela ferramenta JaCoCo e configurada para exigir um mínimo de 80% de cobertura de linha nas classes de negócio.

### Como Executar os Testes

**Pré-requisitos:**
* Java 21 ou superior
* Maven

**Comandos:**

1.  **Executar todos os testes e verificar a cobertura (Comando Principal):**
    * Este comando limpa o projeto, compila, executa todos os testes e, ao final, verifica se a meta de cobertura de 80% foi atingida. O build falhará se a meta não for alcançada.
    ```bash
    # No Windows
    mvnw.cmd clean verify

    # No Linux ou macOS
    ./mvnw clean verify
    ```
    * Após a execução bem-sucedida, o resultado será **"BUILD SUCCESS"**.

2.  **Gerar Apenas o Relatório de Cobertura:**
    * Se desejar apenas gerar o relatório HTML sem executar a verificação que pode falhar o build, use:
    ```bash
    # No Windows
    mvnw.cmd clean test jacoco:report

    # No Linux ou macOS
    ./mvnw clean test jacoco:report
    ```
    * O relatório interativo estará disponível em `target/site/jacoco/index.html`.

### Estratégia de Testes Adotada

-   **Testes Unitários:** Focados na **camada de serviço (`service`)**, utilizando JUnit 5 e Mockito. O objetivo é testar a lógica de negócio de forma isolada e rápida, simulando o comportamento da camada de repositório para evitar a necessidade de um banco de dados real.
-   **Testes de Integração:** Focados na **camada de controller**, utilizando `@SpringBootTest` e `MockMvc`. Estes testes validam o fluxo completo, desde a requisição HTTP até a resposta, incluindo a integração com a camada de segurança (`Spring Security`), validações de DTOs e a persistência real no banco de dados em memória (H2).
-   **Qualidade de Código (Quality Gate):** O projeto está configurado com um "portão de qualidade" via JaCoCo. Qualquer tentativa de construir o projeto (build) falhará se a cobertura de linha das classes de negócio for inferior a 80%, garantindo que novas funcionalidades sejam sempre acompanhadas de testes adequados.


Desenvolvido com ❤️ por 

Ismael Barbosa Galdino Filho.