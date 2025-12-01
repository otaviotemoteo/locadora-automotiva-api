# 🚗 Locadora Automotiva API

API RESTful para gerenciamento de locação de carros, desenvolvida em Java com Spark Framework e MySQL.

## 📋 Requisitos

- **Java 8+**
- **MySQL Server**

---

## 🚀 Como Rodar o Projeto

### 1️⃣ Criar o Banco de Dados

Execute o seguinte script SQL no MySQL para criar a estrutura do banco de dados:

https://docs.google.com/document/d/1mbVCyA0XImAZ9oI1_ebMItDFTe8cONvsZNGTz9ucSJ8/edit?usp=sharing

> **Nota:** Cole o script SQL fornecido e execute no seu MySQL Server ou MySQL Workbench.

### 2️⃣ Configurar a Conexão do Banco

O arquivo `src/util/ConnectionFactory.java` contém as configurações de conexão com o banco. Verifique se as credenciais estão corretas:

```java
// Usuário padrão: root
// Senha padrão: (conforme seu MySQL)
// Banco de dados: locadora_automotiva
```

### 3️⃣ Executar a Aplicação

Execute o arquivo `src/App.java` como aplicação Java.

```bash
java -cp "src:lib/*" App
```

Ou execute diretamente pela IDE (IntelliJ, Eclipse, VS Code):

- Clique com botão direito em `src/App.java`
- Selecione "Run" ou "Executar"

### ✅ Verificar se está rodando

Você verá no console:

```
╔════════════════════════════════════════╗
║   🚗 LOCADORA AUTOMOTIVA API 🚗       ║
╚════════════════════════════════════════╝
🚀 Servidor iniciando na porta 8080...
🌐 Servidor rodando em: http://localhost:8080
```

A API estará disponível em: **http://localhost:8080**

---

## 📚 Endpoints

### 👥 CLIENTES

#### GET `/clientes`

Buscar todos os clientes cadastrados.

**Response:**

```json
[
  {
    "id": 1,
    "nome": "João Silva",
    "email": "joao@example.com",
    "cpf": "12345678900",
    "telefone": "(11) 99999-9999"
  }
]
```

---

#### GET `/cliente/:id`

Buscar cliente por ID.

**Parâmetros:**

- `:id` - ID do cliente (obrigatório)

**Response:**

```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "cpf": "12345678900",
  "telefone": "(11) 99999-9999"
}
```

---

#### POST `/cliente`

Criar novo cliente.

**Body (JSON):**

```json
{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123",
  "cpf": "12345678900",
  "telefone": "(11) 99999-9999"
}
```

**Campos Obrigatórios:**

- `nome` (3-255 caracteres)
- `email` (válido)
- `senha` (3-255 caracteres)
- `cpf` (válido)
- `telefone` (opcional)

**Status de Sucesso:** 201

---

#### PUT `/cliente/:id`

Atualizar cliente existente.

**Parâmetros:**

- `:id` - ID do cliente (obrigatório)

**Body (JSON) - Apenas os campos a atualizar:**

```json
{
  "nome": "João Silva Atualizado",
  "email": "newemail@example.com",
  "telefone": "(11) 88888-8888"
}
```

**Campos Opcionais:**

- `nome` (3-255 caracteres)
- `email` (válido)
- `telefone` (opcional)

> **Nota:** Senha não pode ser alterada por este endpoint.

---

#### DELETE `/cliente/:id`

Excluir cliente.

**Parâmetros:**

- `:id` - ID do cliente (obrigatório)

**Response:**

```json
{
  "mensagem": "Cliente excluído"
}
```

---

### 🚙 CARROS

#### GET `/carros`

Buscar todos os carros disponíveis.

**Response:**

```json
[
  {
    "id": 1,
    "modelo": "Toyota Corolla",
    "marca": "Toyota",
    "ano": 2023,
    "placa": "ABC-1234",
    "valorDiaria": 150.0
  }
]
```

---

#### GET `/carro/:id`

Buscar carro por ID.

**Parâmetros:**

- `:id` - ID do carro (obrigatório)

**Response:**

```json
{
  "id": 1,
  "modelo": "Toyota Corolla",
  "marca": "Toyota",
  "ano": 2023,
  "placa": "ABC-1234",
  "valorDiaria": 150.0
}
```

---

### 📋 ALUGUÉIS

#### GET `/alugueis`

Buscar todos os aluguéis.

**Response:**

```json
[
  {
    "id": 1,
    "clienteId": 1,
    "carroId": 1,
    "dataInicio": "01/12/2024",
    "dataFimPrevista": "05/12/2024",
    "dataFimReal": null,
    "valorTotal": 750.0,
    "status": "APROVADO",
    "dataSolicitacao": "01/12/2024 10:30:00",
    "motivoRejeicao": null
  }
]
```

---

#### GET `/aluguel/:id`

Buscar aluguel por ID.

**Parâmetros:**

- `:id` - ID do aluguel (obrigatório)

---

#### GET `/alugueis/cliente/:clienteId`

Buscar todos os aluguéis de um cliente.

**Parâmetros:**

- `:clienteId` - ID do cliente (obrigatório)
- `?status=APROVADO` (opcional) - Filtrar por status (PENDENTE, APROVADO, REJEITADO, DEVOLVIDO)

**Exemplo:**

```
GET /alugueis/cliente/1?status=APROVADO
```

---

#### POST `/aluguel`

Solicitar um aluguel (criar nova solicitação).

**Body (JSON):**

```json
{
  "clienteId": 1,
  "carroId": 1,
  "diasAluguel": 3
}
```

**Campos Obrigatórios:**

- `clienteId` (ID do cliente)
- `carroId` (ID do carro)
- `diasAluguel` (1-5 dias)

**Status de Sucesso:** 201

**Validações:**

- Cliente deve existir
- Carro deve existir
- Cliente não pode estar suspenso
- Cliente não pode ter outro aluguel ativo
- Carro não pode estar já alugado
- Dias de aluguel entre 1 e 5

---

#### PUT `/aluguel/:id/processar`

Aprovar ou rejeitar uma solicitação de aluguel.

**Parâmetros:**

- `:id` - ID do aluguel (obrigatório)

**Body (JSON) - Para Aprovar:**

```json
{
  "aprovar": true
}
```

**Body (JSON) - Para Rejeitar:**

```json
{
  "aprovar": false,
  "motivoRejeicao": "Cliente não atendeu aos requisitos"
}
```

**Campos Obrigatórios:**

- `aprovar` (true ou false)
- `motivoRejeicao` (5-500 caracteres, obrigatório se aprovar = false)

**Validações:**

- Aluguel deve estar em status PENDENTE
- Processamento deve ser feito em até 24 horas após criação

---

#### PUT `/aluguel/:id/devolver`

Devolver carro alugado.

**Parâmetros:**

- `:id` - ID do aluguel (obrigatório)

**Body:** Vazio (não precisa enviar nada no body)

**Validações:**

- Aluguel deve estar em status APROVADO

**Comportamento:**

- Se devolvido no prazo: Apenas altera status para DEVOLVIDO
- Se atrasado: Altera status para DEVOLVIDO e cria suspensão automática com dias iguais aos dias de atraso

---

#### DELETE `/aluguel/:id`

Deletar aluguel.

**Parâmetros:**

- `:id` - ID do aluguel (obrigatório)

**Response:**

```json
{
  "mensagem": "Aluguel deletado com sucesso"
}
```

---

### 🚫 SUSPENSÕES

#### GET `/suspensoes`

Buscar todas as suspensões.

**Response:**

```json
[
  {
    "id": 1,
    "clienteId": 1,
    "aluguelId": 1,
    "diasSuspensao": "3",
    "dataInicio": "05/12/2024",
    "dataFim": "08/12/2024",
    "motivo": "Atraso de 3 dia(s) na devolução do veículo"
  }
]
```

---

#### GET `/suspensao/:id`

Buscar suspensão por ID.

**Parâmetros:**

- `:id` - ID da suspensão (obrigatório)

---

#### GET `/suspensoes/cliente/:clienteId`

Buscar suspensões de um cliente.

**Parâmetros:**

- `:clienteId` - ID do cliente (obrigatório)
- `?ativas=true` (opcional) - Buscar apenas suspensões ativas

**Exemplo:**

```
GET /suspensoes/cliente/1?ativas=true
```

---

#### GET `/suspensoes/cliente/:clienteId/status`

Verificar se cliente está suspenso.

**Parâmetros:**

- `:clienteId` - ID do cliente (obrigatório)

**Response - Cliente Suspenso:**

```json
{
  "suspenso": true,
  "suspensoes": [...]
}
```

**Response - Cliente Não Suspenso:**

```json
{
  "suspenso": false
}
```

---

#### POST `/suspensao`

Criar nova suspensão.

**Body (JSON):**

```json
{
  "clienteId": 1,
  "aluguelId": 1,
  "diasSuspensao": "5",
  "dataInicio": "05/12/2024",
  "motivo": "Atraso na devolução"
}
```

**Campos Obrigatórios:**

- `clienteId` (ID do cliente)
- `diasSuspensao` (número de dias)
- `motivo` (5-500 caracteres)

**Campos Opcionais:**

- `dataInicio` (padrão: data atual)
- `aluguelId` (opcional)

**Status de Sucesso:** 201

---

#### PUT `/suspensao/:id`

Atualizar suspensão existente.

**Parâmetros:**

- `:id` - ID da suspensão (obrigatório)

**Body (JSON) - Apenas os campos a atualizar:**

```json
{
  "diasSuspensao": "7",
  "motivo": "Atraso aumentado"
}
```

**Campos Opcionais:**

- `diasSuspensao`
- `motivo` (5-500 caracteres)

> **Nota:** Data fim é recalculada automaticamente com base em `diasSuspensao`.

---

#### DELETE `/suspensao/:id`

Deletar suspensão.

**Parâmetros:**

- `:id` - ID da suspensão (obrigatório)

**Response:**

```json
{
  "mensagem": "Suspensão deletada com sucesso"
}
```

---

## 📌 Status HTTP Comuns

| Status | Significado                                   |
| ------ | --------------------------------------------- |
| `200`  | OK - Requisição bem-sucedida                  |
| `201`  | Created - Recurso criado com sucesso          |
| `400`  | Bad Request - Dados inválidos                 |
| `404`  | Not Found - Recurso não encontrado            |
| `409`  | Conflict - Conflito (ex: duplicação de dados) |
| `500`  | Internal Server Error - Erro do servidor      |

---

## 🧪 Testando com Postman

Todos os endpoints estão disponíveis em um **workspace do Postman pronto para usar**.

**Para usar:**

1. Abra o Postman
2. Importe o workspace fornecido
3. Todos os endpoints estarão configurados e prontos para testar

Não é necessário configurar headers manualmente - tudo já está configurado!

---

## 🏗️ Estrutura do Projeto

```
locadora-automotiva-api/
├── src/
│   ├── App.java                 # Classe principal
│   ├── api/
│   │   ├── apiCliente.java      # Endpoints de clientes
│   │   ├── apiCarro.java        # Endpoints de carros
│   │   ├── apiAluguel.java      # Endpoints de aluguéis
│   │   └── apiSuspensao.java    # Endpoints de suspensões
│   ├── dao/
│   │   ├── daoCliente.java
│   │   ├── daoCarro.java
│   │   ├── daoAluguel.java
│   │   └── daoSuspensao.java
│   ├── entities/
│   │   ├── Cliente.java
│   │   ├── Carro.java
│   │   ├── Aluguel.java
│   │   └── Suspensao.java
│   ├── util/
│   │   ├── ConnectionFactory.java
│   │   └── GlobalBrDate.java
│   └── validation/
│       └── Rod.java             # Validações
├── lib/                         # Dependências JAR
└── README.md                    # Este arquivo
```

---

## 🔧 Dependências Utilizadas

- **Spark Framework** - Framework web leve
- **GSON** - Biblioteca JSON
- **MySQL Connector** - Driver JDBC para MySQL

---

## 📄 Licença

Projeto privado.

---
