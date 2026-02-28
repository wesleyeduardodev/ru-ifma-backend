# RU-IFMA Backend

## Stack

Java 21, Spring Boot 3.4.3, Spring Security, Spring Data JPA, PostgreSQL 17

## Como rodar

```bash
docker compose up -d          # sobe PostgreSQL na porta 127.0.0.1:5432
mvn spring-boot:run           # sobe API na porta 8080 (profile dev)
```

Banco local: `ru_ifma` / user: `postgres` / pass: `postgres` (configurado no profile dev)

## Variaveis de ambiente

| Variavel | Descricao | Obrigatorio em prod |
|----------|-----------|---------------------|
| `DB_URL` | URL JDBC do banco | Sim |
| `DB_USER` | Usuario do banco | Sim |
| `DB_PASSWORD` | Senha do banco | Sim |
| `SPRING_PROFILES_ACTIVE` | Profile ativo (`dev` ou `prod`) | Sim |
| `CORS_ORIGINS` | Origens CORS separadas por virgula | Sim |

## Profiles

- **dev** (padrao): `show-sql=true`, `ddl-auto=update`, fallback de credenciais para localhost
- **prod**: `show-sql=false`, `ddl-auto=validate`, `stacktrace=never`, sem fallback de credenciais

## Arquitetura

```
src/main/java/br/edu/ifma/ru/
  RuIfmaBackendApplication.java → Classe principal (@EnableScheduling)
  config/
    SecurityConfig.java    → Autenticacao, headers de seguranca, BCrypt 12
    CorsConfig.java        → CORS via variavel de ambiente CORS_ORIGINS
    RateLimitFilter.java   → Rate limiting por IP (5 login/min, 60 geral/min)
  entity/
    Administrador.java     → Entidade JPA
    Cardapio.java          → Entidade JPA
    TipoRefeicao.java      → Enum (ALMOCO, JANTAR)
  repository/
    AdministradorRepository.java
    CardapioRepository.java
  service/
    AdministradorService.java → CRUD admin + protecao do admin principal
    CardapioService.java      → CRUD cardapio
    AuthService.java          → Autenticacao com protecao contra timing attack
  controller/
    AdminController.java     → @Validated, @Positive nos IDs
    CardapioController.java  → @Validated, @Positive nos IDs
    AuthController.java      → Login e /me (usa AuthService)
  dto/
    request/  → CardapioRequest, AdminRequest, LoginRequest (com @Size)
    response/ → CardapioResponse, AdminResponse, LoginResponse
  exception/
    GlobalExceptionHandler.java → Handlers: 404, 409, 400, 500 (fallback generico)
    ResourceNotFoundException.java
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

## Regras de negocio

- Admin principal (`admin@ifma.edu.br`) nao pode ser excluido
- Sistema deve ter pelo menos 1 administrador (exclusao bloqueada se for o ultimo)
- Rate limit: 5 tentativas de login por minuto por IP, 60 requisicoes gerais por minuto

## Seguranca

- HTTP Basic Auth via Spring Security
- Senhas com BCrypt strength 12 (PasswordEncoder bean)
- UserDetailsService busca por email na tabela administradores
- CORS configurado via variavel de ambiente `CORS_ORIGINS`
- CSRF desabilitado (API stateless)
- Sessao stateless (SessionCreationPolicy.STATELESS)
- Headers: HSTS, CSP, X-Content-Type-Options, X-Frame-Options, Referrer-Policy
- Rate limiting via RateLimitFilter com limpeza periodica de memoria
- Protecao contra timing attack no login (AuthService)
- Mensagens de erro genericas (sem exposicao de IDs ou stacktraces)
- Validacao @Size em todos os DTOs, @Positive nos PathVariables

## Deploy

- Hospedado no Railway
- Dockerfile multi-stage com usuario nao-root (appuser)
- docker-compose.yml local com healthcheck e porta restrita a 127.0.0.1

## Convencoes

- Controller -> Service -> Repository (nunca pular camadas)
- DTOs de API: sufixo Request/Response, sempre Java Records
- Validacao com Jakarta Validation (@NotBlank, @NotNull, @Email, @Size, @Positive)
- Excecoes tratadas no GlobalExceptionHandler (404, 409, 400, 500)
- Codigo em portugues, sem comentarios, sem emojis
- Imports organizados no topo, SOLID aplicado
