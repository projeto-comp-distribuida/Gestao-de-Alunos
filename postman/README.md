# 📮 Coleção Postman - DistriSchool Student Management API

## 📋 Visão Geral

Esta coleção do Postman contém **todos os testes** para a API de Gestão de Estudantes do DistriSchool, com **máxima organização** e cobertura completa de cenários.

## 🗂️ Estrutura da Coleção

### 1. 🏥 Health Check
- ✅ Verificação de saúde do serviço
- ✅ Testes automatizados de disponibilidade

### 2. 📝 CRUD - Students
- ✅ **Criar Estudante** (3 variações com diferentes cursos)
- ✅ **Listar Todos** (com paginação)
- ✅ **Buscar por ID**
- ✅ **Atualizar Estudante**
- ✅ **Deletar Estudante** (soft delete)

### 3. 🔍 Busca e Filtros
- ✅ Buscar por **Nome** (parcial)
- ✅ Buscar por **Email** (exato)
- ✅ Buscar por **Matrícula**
- ✅ Buscar por **Curso**
- ✅ Filtrar por **Status** (ACTIVE, INACTIVE, etc.)

### 4. 📊 Estatísticas
- ✅ Estatísticas gerais
- ✅ Estatísticas por curso
- ✅ Contagem total de estudantes

### 5. ❌ Testes de Validação
- ✅ CPF inválido
- ✅ Email inválido
- ✅ Campos obrigatórios vazios
- ✅ ID inexistente (404)
- ✅ CPF duplicado

### 6. 🔄 Operações em Lote
- ✅ Criação de múltiplos estudantes
- ✅ Testes de volume

## 🚀 Como Usar

### Passo 1: Importar no Postman

1. Abra o **Postman**
2. Clique em **Import** (canto superior esquerdo)
3. Arraste os arquivos:
   - `DistriSchool-Student-API.postman_collection.json`
   - `DistriSchool-Local.postman_environment.json`
4. Clique em **Import**

### Passo 2: Configurar Environment

1. No canto superior direito, selecione **"DistriSchool - Local Environment"**
2. Verifique se `base_url` está configurado como `http://localhost:8080`

### Passo 3: Executar os Testes

#### Opção A: Executar Toda a Coleção

1. Clique com botão direito na coleção **"DistriSchool - Student Management API"**
2. Selecione **"Run collection"**
3. Clique em **"Run DistriSchool - Student Management API"**
4. Aguarde todos os testes serem executados

#### Opção B: Executar Testes Individuais

1. Navegue pela estrutura de pastas
2. Clique em uma requisição específica
3. Clique em **"Send"**
4. Visualize a resposta e os testes na aba **"Test Results"**

### Passo 4: Ordem Recomendada

Para melhor experiência, execute nesta ordem:

1. **🏥 Health Check** → Verifica se o serviço está UP
2. **📝 CRUD - 1. Criar Estudante - Completo** → Cria primeiro estudante
3. **📝 CRUD - 4. Listar Todos os Estudantes** → Verifica se foi criado
4. **📝 CRUD - 5. Buscar Estudante por ID** → Busca o estudante criado
5. **📝 CRUD - 6. Atualizar Estudante** → Atualiza dados
6. **🔍 Busca e Filtros** → Testa todos os filtros
7. **📊 Estatísticas** → Verifica estatísticas
8. **❌ Testes de Validação** → Testa casos de erro
9. **🔄 Operações em Lote** → Cria estudantes adicionais

## 🔧 Variáveis Disponíveis

### Variáveis de Collection
- `{{base_url}}` - URL base da API
- `{{student_id}}` - ID do último estudante criado (auto-preenchido)
- `{{registration_number}}` - Matrícula do último estudante (auto-preenchido)

### Variáveis de Environment
- `{{base_url}}` - http://localhost:8080
- `{{api_version}}` - v1
- `{{kafka_ui_url}}` - http://localhost:8090
- `{{postgres_host}}` - localhost
- `{{postgres_port}}` - 5432
- `{{redis_host}}` - localhost
- `{{redis_port}}` - 6379

## 📊 Testes Automatizados

Cada requisição inclui **testes automatizados** que verificam:

- ✅ **Status Code** correto (200, 201, 400, 404)
- ✅ **Estrutura da resposta** (campos obrigatórios presentes)
- ✅ **Tipo de dados** (strings, números, booleanos)
- ✅ **Valores esperados** (dados corretos após operações)
- ✅ **Mensagens de erro** (validações funcionando)

### Exemplo de Teste Automatizado:

```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Student has correct data", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.data.fullName).to.eql("Maria Oliveira Santos");
    pm.expect(jsonData.data.status).to.eql("ACTIVE");
});
```

## 📦 Dados de Teste

### Estudantes Pré-configurados

1. **Maria Oliveira Santos**
   - Curso: Ciência da Computação
   - Semestre: 3
   - Status: Bolsista PROUNI

2. **Pedro Henrique Costa**
   - Curso: Engenharia Elétrica
   - Semestre: 6
   - Nota: Monitor de Circuitos

3. **Ana Paula Fernandes**
   - Curso: Administração
   - Semestre: 4
   - Nota: Presidente do CA

4. **Carlos Eduardo Lima**
   - Curso: Direito
   - Semestre: 8
   - Nota: Formando 2025

5. **Juliana Santos Rodrigues**
   - Curso: Medicina
   - Semestre: 2
   - Nota: Intercâmbio aprovado

6. **Roberto Alves Pereira**
   - Curso: Arquitetura
   - Semestre: 10
   - Nota: TCC em andamento

## 🎯 Cenários de Teste Cobertos

### ✅ Casos de Sucesso
- Criação de estudante completo
- Listagem com paginação
- Busca por ID, email, matrícula
- Atualização de dados
- Soft delete
- Filtros por curso e status
- Estatísticas

### ❌ Casos de Erro
- Validação de CPF (11 dígitos)
- Validação de email (formato correto)
- Campos obrigatórios vazios
- Busca de ID inexistente
- Duplicação de CPF
- Telefone com formato incorreto

### 🔍 Casos de Borda
- Busca com resultado vazio
- Paginação no limite
- Caracteres especiais em nomes
- Datas no passado/futuro

## 🎨 Formatação das Respostas

Todas as respostas seguem o padrão:

```json
{
  "success": true,
  "message": "Operação realizada com sucesso",
  "data": { ... },
  "timestamp": "2025-10-14T13:29:45.639199509"
}
```

## 🐛 Troubleshooting

### Erro: "Could not send request"
**Solução:** Verifique se o Docker Compose está rodando:
```bash
docker compose ps
```

### Erro: Connection Refused
**Solução:** Aguarde o serviço iniciar completamente (~70 segundos)

### Erro: 404 Not Found
**Solução:** Verifique se a URL base está correta: `http://localhost:8080`

### Testes Falhando
**Solução:** Execute os testes na ordem recomendada, pois alguns dependem de dados criados anteriormente

## 📚 Recursos Adicionais

- **Kafka UI**: http://localhost:8090 (ver eventos publicados)
- **PostgreSQL**: localhost:5432 (verificar dados persistidos)
- **Redis**: localhost:6379 (verificar cache)

## 🔐 Segurança

⚠️ **IMPORTANTE**: Esta coleção é para ambiente de **desenvolvimento local**. 

Para ambientes de produção:
- Adicione autenticação (JWT, OAuth2)
- Use HTTPS
- Configure rate limiting
- Implemente logs de auditoria

## 📈 Métricas de Cobertura

- ✅ **100% dos endpoints** cobertos
- ✅ **Todos os métodos HTTP** testados (GET, POST, PUT, DELETE)
- ✅ **Todos os códigos de status** validados (200, 201, 400, 404)
- ✅ **Validações completas** de entrada/saída
- ✅ **Testes automatizados** em todas as requisições

## 🤝 Contribuindo

Para adicionar novos testes:

1. Crie uma nova pasta dentro da coleção
2. Adicione requisições com descrições claras
3. Inclua testes automatizados (aba "Tests")
4. Documente casos de uso no README

## 📞 Suporte

Em caso de problemas:
1. Verifique os logs: `docker compose logs student-management-service-dev`
2. Consulte a documentação da API
3. Abra uma issue no repositório

---

**Versão:** 1.0.0  
**Última Atualização:** 14 de Outubro de 2025  
**Autor:** DistriSchool Team

