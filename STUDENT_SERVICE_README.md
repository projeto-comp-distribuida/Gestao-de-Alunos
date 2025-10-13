# 🎓 Student Management Service - Sistema de Gerenciamento de Alunos

Sistema completo de gerenciamento de alunos para faculdade, desenvolvido usando o template DistriSchool.

## 📋 Funcionalidades

### Gestão de Alunos
- ✅ **CRUD Completo** de alunos
- ✅ **Soft Delete** - Exclusão lógica com possibilidade de restauração
- ✅ **Busca Avançada** - Filtros por nome, curso, semestre e status
- ✅ **Validações** - CPF, email e matrícula únicos
- ✅ **Auditoria** - Rastreamento completo de criação e modificações
- ✅ **Geração Automática** de número de matrícula

### Dados do Aluno
- **Dados Pessoais**: Nome completo, CPF, email, telefone, data de nascimento
- **Dados Acadêmicos**: Matrícula, curso, semestre, data de ingresso, status
- **Endereço Completo**: Rua, número, complemento, bairro, cidade, estado, CEP
- **Contato de Emergência**: Nome, telefone e relacionamento
- **Observações**: Campo de texto livre para anotações

### Status do Aluno
- `ACTIVE` - Ativo
- `INACTIVE` - Inativo
- `GRADUATED` - Formado
- `SUSPENDED` - Suspenso
- `TRANSFERRED` - Transferido
- `DROPPED` - Desistente

## 🚀 Tecnologias Utilizadas

- **Spring Boot 3.2.0** - Framework principal
- **Hibernate/JPA** - ORM para persistência
- **PostgreSQL** - Banco de dados relacional
- **Flyway** - Migrations de banco de dados
- **Redis** - Cache distribuído
- **Apache Kafka** - Mensageria assíncrona
- **Spring Cloud OpenFeign** - Comunicação entre microserviços
- **Resilience4j** - Circuit breaker e resiliência
- **Lombok** - Redução de boilerplate
- **Micrometer/Prometheus** - Métricas e monitoramento

## 📡 Endpoints da API

### Criar Aluno
```http
POST /api/v1/students
Content-Type: application/json
X-User-Id: user123

{
  "fullName": "João Silva Santos",
  "cpf": "12345678901",
  "email": "joao.silva@faculdade.edu.br",
  "phone": "11987654321",
  "birthDate": "2000-05-15",
  "course": "Ciência da Computação",
  "semester": 3,
  "enrollmentDate": "2023-02-01",
  "addressStreet": "Rua das Flores",
  "addressNumber": "123",
  "addressCity": "São Paulo",
  "addressState": "SP",
  "addressZipcode": "01234567",
  "emergencyContactName": "Maria Silva",
  "emergencyContactPhone": "11987654322",
  "emergencyContactRelationship": "Mãe"
}
```

### Listar Alunos
```http
GET /api/v1/students?page=0&size=20&sortBy=fullName&direction=ASC
```

### Buscar Aluno por ID
```http
GET /api/v1/students/{id}
```

### Buscar Aluno por Matrícula
```http
GET /api/v1/students/registration/{registrationNumber}
```

### Buscar com Filtros
```http
GET /api/v1/students/search?name=João&course=Ciência da Computação&semester=3&status=ACTIVE
```

### Buscar por Curso
```http
GET /api/v1/students/course/Ciência da Computação
```

### Buscar por Curso e Semestre
```http
GET /api/v1/students/course/Ciência da Computação/semester/3
```

### Atualizar Aluno
```http
PUT /api/v1/students/{id}
Content-Type: application/json
X-User-Id: user123

{
  "fullName": "João Silva Santos Jr",
  "semester": 4,
  ...
}
```

### Atualizar Status
```http
PATCH /api/v1/students/{id}/status?status=GRADUATED
X-User-Id: user123
```

### Deletar Aluno (Soft Delete)
```http
DELETE /api/v1/students/{id}
X-User-Id: user123
```

### Restaurar Aluno
```http
POST /api/v1/students/{id}/restore
X-User-Id: user123
```

### Estatísticas
```http
GET /api/v1/students/statistics
```

### Contar por Curso
```http
GET /api/v1/students/count/course/{course}
```

## 🎯 Eventos Kafka

O serviço publica os seguintes eventos:

### student.created
Publicado quando um novo aluno é criado.

### student.updated
Publicado quando um aluno é atualizado.

### student.status.changed
Publicado quando o status de um aluno muda.

### student.deleted
Publicado quando um aluno é deletado (soft delete).

### Estrutura do Evento
```json
{
  "eventId": "uuid",
  "eventType": "student.created",
  "serviceName": "student-management-service",
  "timestamp": "2024-10-13T10:30:00",
  "metadata": {},
  "data": {
    "studentId": 1,
    "fullName": "João Silva Santos",
    "email": "joao.silva@faculdade.edu.br",
    "registrationNumber": "20241001",
    "course": "Ciência da Computação",
    "semester": 3,
    "status": "ACTIVE"
  }
}
```

## 🗄️ Modelo de Dados

### Tabela: students

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGSERIAL | ID único (chave primária) |
| full_name | VARCHAR(255) | Nome completo |
| cpf | VARCHAR(11) | CPF (único) |
| email | VARCHAR(255) | Email (único) |
| phone | VARCHAR(11) | Telefone |
| birth_date | DATE | Data de nascimento |
| registration_number | VARCHAR(50) | Número de matrícula (único) |
| course | VARCHAR(255) | Curso |
| semester | INTEGER | Semestre (1-20) |
| enrollment_date | DATE | Data de ingresso |
| status | VARCHAR(20) | Status do aluno |
| address_* | VARCHAR | Campos de endereço |
| emergency_contact_* | VARCHAR | Contato de emergência |
| notes | TEXT | Observações |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data de atualização |
| created_by | VARCHAR(255) | Quem criou |
| updated_by | VARCHAR(255) | Quem atualizou |
| deleted_at | TIMESTAMP | Data de exclusão (soft delete) |
| deleted_by | VARCHAR(255) | Quem deletou |

## 🔧 Configuração e Execução

### Pré-requisitos
- Java 17+
- Docker e Docker Compose
- Maven

### Executar com Docker Compose (Desenvolvimento)
```bash
# Subir todos os serviços (PostgreSQL, Redis, Kafka, App)
docker-compose -f docker-compose-dev.yml up --build

# Ou usar o script
./scripts/dev-docker.sh
```

### Executar Localmente
```bash
# Build
mvn clean install

# Executar
mvn spring-boot:run
```

### Variáveis de Ambiente
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/distrischool_students
SPRING_DATASOURCE_USERNAME=distrischool
SPRING_DATASOURCE_PASSWORD=distrischool123
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379
SERVER_PORT=8080
```

## 📊 Cache Redis

O serviço utiliza Redis para cache dos seguintes dados:
- Busca de alunos por ID (TTL: 10 minutos)
- Busca por número de matrícula (TTL: 10 minutos)

O cache é automaticamente invalidado quando:
- Um aluno é criado
- Um aluno é atualizado
- Um aluno é deletado
- O status é alterado

## 🧪 Testando a API

### Criar um aluno
```bash
curl -X POST http://localhost:8080/api/v1/students \
  -H "Content-Type: application/json" \
  -H "X-User-Id: admin" \
  -d '{
    "fullName": "Maria Oliveira",
    "cpf": "98765432100",
    "email": "maria.oliveira@faculdade.edu.br",
    "phone": "11912345678",
    "birthDate": "2001-08-20",
    "course": "Engenharia de Software",
    "semester": 2,
    "enrollmentDate": "2024-02-01"
  }'
```

### Listar alunos
```bash
curl http://localhost:8080/api/v1/students
```

### Buscar aluno por ID
```bash
curl http://localhost:8080/api/v1/students/1
```

### Buscar com filtros
```bash
curl "http://localhost:8080/api/v1/students/search?course=Engenharia de Software&status=ACTIVE"
```

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

## 📈 Monitoramento

### Métricas Prometheus
```bash
curl http://localhost:8080/actuator/prometheus
```

### Health Checks
```bash
# Liveness
curl http://localhost:8080/actuator/health/liveness

# Readiness
curl http://localhost:8080/actuator/health/readiness
```

### Métricas Customizadas
- `students.create` - Tempo para criar um aluno
- `students.get` - Tempo para buscar um aluno
- `students.list` - Tempo para listar alunos
- `students.update` - Tempo para atualizar um aluno
- `students.delete` - Tempo para deletar um aluno

## 🔍 Logs

Os logs são estruturados e incluem:
- Nível DEBUG para operações de serviço
- Nível INFO para operações de Kafka
- Todas as operações CRUD são logadas
- Eventos Kafka são logados com sucesso/falha

## 🐛 Troubleshooting

### Erro de conexão com PostgreSQL
Verifique se o PostgreSQL está rodando e as credenciais estão corretas.

### Erro de conexão com Kafka
Verifique se o Kafka está rodando na porta 9092.

### Migrations Flyway falhando
Execute: `mvn flyway:repair` e tente novamente.

### Cache Redis não funciona
Verifique se o Redis está rodando na porta 6379.

## 📝 Próximos Passos

- [ ] Implementar paginação de endereços múltiplos
- [ ] Adicionar histórico de mudanças de curso
- [ ] Implementar sistema de notas
- [ ] Adicionar controle de frequência
- [ ] Integrar com sistema de matrículas em disciplinas
- [ ] Adicionar upload de foto do aluno
- [ ] Implementar geração de relatórios
- [ ] Adicionar autenticação JWT

## 📄 Licença

Este projeto faz parte do DistriSchool - Sistema de Gestão Escolar Distribuído.

---

**Desenvolvido com ❤️ usando Spring Boot e o template DistriSchool**

