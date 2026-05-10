# Eventos API

API REST para gerenciamento de ambientes e reservas. O sistema permite cadastrar salas, criar reservas, bloquear conflitos de horario, consultar reservas paginadas, filtrar reservas por periodo e consultar horarios/salas disponiveis.

## Tecnologias

- Java 17
- Spring Boot 3.5.14
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Flyway
- Lombok
- Maven
- Docker Compose

## Banco de Dados

O projeto usa PostgreSQL via Docker Compose.

Para subir o banco:

```bash
docker compose up -d
```

Configuracao padrao do banco:

```text
Host: localhost
Porta: 5432
Database: eventos
Usuario: postgres
Senha: postgres
```

Variaveis aceitas pelo `compose.yaml`:

```text
POSTGRES_DB
DB_USERNAME
DB_PASSWORD
POSTGRES_PORT
```

Para derrubar o banco, remover volumes e subir novamente:

```bash
./reset-container.sh
```

No Windows, se estiver usando Git Bash:

```bash
sh reset-container.sh
```

## Migrations

As migrations ficam em:

```text
src/main/resources/db/migration
```

Arquivos atuais:

```text
V1__INICIANDO_TABELAS.sql
V2__POPULANDO_AMBIENTES_E_RESERVAS.sql
```

O Flyway executa automaticamente as migrations ao iniciar a aplicacao.

## Executando a API

Com o banco ativo, execute:

```bash
./mvnw spring-boot:run
```

Ou, no Windows:

```bash
mvnw.cmd spring-boot:run
```

URL base:

```text
http://localhost:8080
```

## Respostas de Erro

Formato padrao de erro:

```json
{
  "mensagem": "Mensagem do erro"
}
```

Principais status:

```text
400 Bad Request - erro de regra de negocio, conflito de horario ou horario invalido
404 Not Found - recurso nao encontrado
```

## Rotas de Ambientes

### Criar Ambiente

```http
POST /ambientes
```

Body:

```json
{
  "tipo": "Sala de reuniao",
  "descricao": "Sala 01"
}
```

Resposta:

```text
201 Created
```

### Listar Ambientes

```http
GET /ambientes
```

Paginacao padrao:

```text
size=10
sort=id,desc
```

Exemplos:

```http
GET /ambientes
GET /ambientes?page=0&size=10
GET /ambientes?page=0&size=10&sort=id,asc
```

Resposta:

```text
200 OK
```

### Buscar Ambiente por ID

```http
GET /ambientes/{id}
```

Exemplo:

```http
GET /ambientes/1
```

Resposta:

```text
200 OK
```

### Deletar Ambiente

```http
DELETE /ambientes/{id}
```

Exemplo:

```http
DELETE /ambientes/1
```

Resposta:

```text
204 No Content
```

## Rotas de Reservas

### Criar Reserva

```http
POST /reservas
```

Body:

```json
{
  "dataDeReserva": "2026-05-03",
  "dataReservadaInicio": "2026-05-03T09:00:00",
  "dataReservadaFim": "2026-05-03T12:00:00",
  "descricaoReserva": "Reuniao de planejamento",
  "ambienteId": 1
}
```

Resposta:

```text
201 Created
```

Observacao: o sistema bloqueia reservas com conflito de horario para o mesmo ambiente.

### Listar Reservas

```http
GET /reservas
```

Paginacao padrao:

```text
size=10
sort=dataReservadaInicio,desc
```

Exemplos:

```http
GET /reservas
GET /reservas?page=0&size=10
GET /reservas?page=0&size=10&sort=dataReservadaInicio,asc
```

Resposta:

```text
200 OK
```

### Buscar Reserva por ID

```http
GET /reservas/{id}
```

Exemplo:

```http
GET /reservas/1
```

Resposta:

```text
200 OK
```

### Atualizar Reserva

```http
PUT /reservas/{id}
```

Exemplo:

```http
PUT /reservas/1
```

Body:

```json
{
  "dataDeReserva": "2026-05-03",
  "dataReservadaInicio": "2026-05-03T10:00:00",
  "dataReservadaFim": "2026-05-03T11:00:00",
  "descricaoReserva": "Reuniao atualizada",
  "ambienteId": 1
}
```

Resposta:

```text
200 OK
```

### Deletar Reserva

```http
DELETE /reservas/{id}
```

Exemplo:

```http
DELETE /reservas/1
```

Resposta:

```text
204 No Content
```

### Listar Reservas por Periodo

Lista reservas cuja `dataReservadaInicio` esteja entre a data inicial e final.

```http
GET /reservas/periodo?inicio={yyyy-MM-dd}&fim={yyyy-MM-dd}
```

Paginacao padrao:

```text
size=10
sort=dataReservadaInicio,asc
```

Exemplos:

```http
GET /reservas/periodo?inicio=2026-05-01&fim=2026-05-31
GET /reservas/periodo?inicio=2026-05-01&fim=2026-05-31&page=0&size=10
```

Resposta:

```text
200 OK
```

### Consultar Horarios Disponiveis de uma Sala

Retorna as faixas de horario livres de uma sala em um dia.

```http
GET /reservas/horarios-disponiveis?ambienteId={id}&data={yyyy-MM-dd}
```

Exemplo:

```http
GET /reservas/horarios-disponiveis?ambienteId=1&data=2026-05-03
```

Resposta:

```json
[
  {
    "inicio": "00:00:00",
    "fim": "08:59:00"
  },
  {
    "inicio": "12:01:00",
    "fim": "23:59:00"
  }
]
```

### Consultar Salas Disponiveis

Retorna todas as salas com suas respectivas faixas de horario livres em um dia.

```http
GET /reservas/salas-disponiveis?data={yyyy-MM-dd}
```

Exemplo:

```http
GET /reservas/salas-disponiveis?data=2026-05-03
```

Resposta:

```json
[
  {
    "sala": {
      "id": 1,
      "tipo": "Sala de reuniao",
      "descricao": "Sala 01 - Reunioes pequenas"
    },
    "horariosDisponiveis": [
      {
        "inicio": "00:00:00",
        "fim": "08:59:00"
      },
      {
        "inicio": "12:01:00",
        "fim": "23:59:00"
      }
    ]
  }
]
```

## Paginacao

Endpoints paginados retornam `Page`, com metadados como:

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "size": 10,
  "number": 0
}
```

Parametros comuns:

```text
page - numero da pagina, iniciando em 0
size - quantidade de elementos por pagina
sort - campo e direcao, exemplo: sort=dataReservadaInicio,asc
```

## Regras de Reserva

- Uma reserva precisa ter horario inicial anterior ao horario final.
- Nao e permitido criar ou atualizar uma reserva com conflito de horario no mesmo ambiente.
- A consulta de disponibilidade considera faixas livres no formato fechado por minuto.
- Exemplo: se existe reserva das `09:00` as `12:00`, os horarios livres retornam `00:00 - 08:59` e `12:01 - 23:59`.
