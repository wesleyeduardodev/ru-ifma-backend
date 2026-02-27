# RU-IFMA Backend

## Stack

Java 21, Spring Boot 3.4.3, Spring Security, Spring Data JPA, PostgreSQL 17

## Como rodar

```bash
docker compose up -d          # sobe PostgreSQL na porta 5432
mvn spring-boot:run           # sobe API na porta 8080
```

Banco: `ru_ifma` / user: `postgres` / pass: `postgres`

## Arquitetura

```
src/main/java/br/edu/ifma/ru/
  config/        → SecurityConfig, CorsConfig
  entity/        → Administrador, Cardapio, TipoRefeicao (enum)
  repository/    → AdministradorRepository, CardapioRepository
  service/       → AdministradorService, CardapioService
  controller/    → AdminController, CardapioController, AuthController
  dto/
    request/     → CardapioRequest, AdminRequest, LoginRequest
    response/    → CardapioResponse, AdminResponse, LoginResponse
  exception/     → GlobalExceptionHandler, ResourceNotFoundException
  seed/          → DataSeeder (cria admin padrao no startup)
```

## Entidades

### Administrador (tabela `administradores`)
| Campo    | Tipo          | Constraint      |
|----------|---------------|-----------------|
| id       | Long          | PK, auto        |
| nome     | String        | not null         |
| email    | String        | not null, unique |
| senha    | String        | not null (BCrypt)|
| criadoEm | LocalDateTime | auto, imutavel  |

### Cardapio (tabela `cardapios`)
| Campo          | Tipo          | Constraint                    |
|----------------|---------------|-------------------------------|
| id             | Long          | PK, auto                      |
| data           | LocalDate     | not null                      |
| tipoRefeicao   | TipoRefeicao  | not null (ALMOCO ou JANTAR)   |
| pratoPrincipal | String        | not null                      |
| acompanhamento | String        | not null                      |
| salada         | String        | not null                      |
| sobremesa      | String        | not null                      |
| suco           | String        | not null                      |
| criadoEm       | LocalDateTime | auto, imutavel                |
| atualizadoEm   | LocalDateTime | auto                          |

Constraint unique: (data + tipoRefeicao)

## Endpoints

### Publicos (sem autenticacao)
| Metodo | Rota                        | Descricao                  |
|--------|-----------------------------|----------------------------|
| GET    | /api/cardapios?data=YYYY-MM-DD | Lista cardapios da data |
| GET    | /api/cardapios/{id}         | Detalhe de um cardapio     |
| POST   | /api/auth/login             | Login (retorna dados admin) |

### Protegidos (Basic Auth)
| Metodo | Rota                        | Descricao                  |
|--------|-----------------------------|----------------------------|
| POST   | /api/cardapios              | Criar cardapio             |
| PUT    | /api/cardapios/{id}         | Atualizar cardapio         |
| DELETE | /api/cardapios/{id}         | Deletar cardapio           |
| GET    | /api/admin                  | Listar administradores     |
| GET    | /api/admin/{id}             | Detalhe admin              |
| POST   | /api/admin                  | Criar admin                |
| PUT    | /api/admin/{id}             | Atualizar admin            |
| DELETE | /api/admin/{id}             | Deletar admin              |
| GET    | /api/auth/me                | Dados do admin logado      |

## Seguranca

- HTTP Basic Auth via Spring Security
- Senhas com BCrypt (PasswordEncoder bean)
- UserDetailsService busca por email na tabela administradores
- CORS liberado para `http://localhost:5173`
- CSRF desabilitado (API stateless)
- Sessao stateless (SessionCreationPolicy.STATELESS)

## Seed

DataSeeder cria no startup (se nao existir):
- Email: `admin@ifma.edu.br`
- Senha: `admin123`

## Convencoes

- Controller -> Service -> Repository (nunca pular camadas)
- DTOs de API: sufixo Request/Response, sempre Java Records
- Validacao com Jakarta Validation (@NotBlank, @NotNull, @Email)
- Excecoes tratadas no GlobalExceptionHandler (404, 409, 400)
- Codigo em portugues, sem comentarios, sem emojis
- Imports organizados no topo, SOLID aplicado
