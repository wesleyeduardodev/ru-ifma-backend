# RU-IFMA Backend

API REST do sistema de cardapios do Restaurante Universitario do IFMA.

## Tecnologias

- Java 21
- Spring Boot 3.4.3
- Spring Security (HTTP Basic Auth)
- Spring Data JPA
- PostgreSQL 17
- Jakarta Validation

## Pre-requisitos

- Java 21 (JDK)
- Maven 3.9+
- Docker e Docker Compose (para o banco de dados)

## Instalacao

```bash
git clone https://github.com/seu-usuario/ru-ifma.git
cd ru-ifma/ru-ifma-backend
```

## Configuracao

### Banco de dados (local)

Suba o PostgreSQL via Docker Compose:

```bash
docker compose up -d
```

O banco fica acessivel em `127.0.0.1:5432` com:
- Database: `ru_ifma`
- Usuario: `postgres`
- Senha: `postgres`

### Variaveis de ambiente

| Variavel | Descricao | Obrigatorio em producao |
|----------|-----------|-------------------------|
| `DB_URL` | URL JDBC do banco | Sim |
| `DB_USER` | Usuario do banco | Sim |
| `DB_PASSWORD` | Senha do banco | Sim |
| `SPRING_PROFILES_ACTIVE` | Profile ativo (`dev` ou `prod`) | Sim |
| `CORS_ORIGINS` | Origens permitidas, separadas por virgula | Sim |

Em desenvolvimento, o profile `dev` usa valores padrao para o banco local. Em producao, todas as variaveis sao obrigatorias (sem fallback).

## Executando

```bash
mvn spring-boot:run
```

A API fica disponivel em http://localhost:8080

## Profiles

- **dev** (padrao): SQL visivel no log, `ddl-auto=update`, banco local com fallback
- **prod**: SQL oculto, `ddl-auto=validate`, sem stacktrace nas respostas, sem fallback de credenciais

## Endpoints

### Publicos

| Metodo | Rota | Descricao |
|--------|------|-----------|
| GET | `/api/cardapios?data=YYYY-MM-DD` | Cardapios de uma data |
| GET | `/api/cardapios/{id}` | Detalhe de um cardapio |
| POST | `/api/auth/login` | Login |

### Protegidos (Basic Auth)

| Metodo | Rota | Descricao |
|--------|------|-----------|
| POST | `/api/cardapios` | Criar cardapio |
| PUT | `/api/cardapios/{id}` | Atualizar cardapio |
| DELETE | `/api/cardapios/{id}` | Deletar cardapio |
| GET | `/api/admin` | Listar administradores |
| GET | `/api/admin/{id}` | Detalhe de um admin |
| POST | `/api/admin` | Criar admin |
| PUT | `/api/admin/{id}` | Atualizar admin |
| DELETE | `/api/admin/{id}` | Deletar admin |
| GET | `/api/auth/me` | Dados do admin logado |

## Regras de negocio

- O administrador principal (`admin@ifma.edu.br`) nao pode ser excluido
- O sistema deve ter pelo menos 1 administrador
- Nao pode existir dois cardapios para a mesma data e tipo de refeicao
- Senhas devem ter entre 8 e 72 caracteres

## Seguranca

- Autenticacao via HTTP Basic Auth com BCrypt (strength 12)
- Rate limiting: 5 tentativas de login/min e 60 requisicoes/min por IP
- Headers de seguranca: HSTS, CSP, X-Content-Type-Options, X-Frame-Options
- CORS restrito via variavel de ambiente
- Validacao de entrada em todos os DTOs (@Size, @NotBlank, @Positive)
- Mensagens de erro genericas (sem exposicao de dados internos)
- Protecao contra timing attack no login

## Estrutura do projeto

```
src/main/java/br/edu/ifma/ru/
  config/           SecurityConfig, CorsConfig, RateLimitFilter
  controller/       AdminController, CardapioController, AuthController
  service/          AdministradorService, CardapioService, AuthService
  repository/       AdministradorRepository, CardapioRepository
  entity/           Administrador, Cardapio, TipoRefeicao
  dto/request/      AdminRequest, CardapioRequest, LoginRequest
  dto/response/     AdminResponse, CardapioResponse, LoginResponse
  exception/        GlobalExceptionHandler, ResourceNotFoundException
```

## Deploy

O backend esta hospedado no **Railway** com:

- Dockerfile multi-stage (build + runtime com usuario nao-root)
- Variaveis de ambiente configuradas no painel do Railway
- Profile `prod` ativo

## Licenca

Projeto academico - IFMA
