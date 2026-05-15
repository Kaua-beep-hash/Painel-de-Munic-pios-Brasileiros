# 🗺️ Municípios BR — Painel IBGE

[![CI — Municípios BR](https://github.com/SEU_USUARIO/municipios-br/actions/workflows/ci.yml/badge.svg)](https://github.com/SEU_USUARIO/municipios-br/actions/workflows/ci.yml)

> 🔗 **Deploy:** [INSERIR LINK APÓS DEPLOY] ← _clique aqui para acessar a aplicação_

---

## 📌 Sobre o Projeto

O **Municípios BR** é um painel web interativo que permite consultar informações detalhadas sobre todos os **5.570 municípios brasileiros** diretamente no navegador. A aplicação consome a **API pública e gratuita de Localidades do IBGE** em tempo real, exibindo dados como nome, código IBGE e microrregião de cada município.

### Problema Resolvido

Acessar dados geográficos oficiais sobre municípios brasileiros geralmente exige navegar por portais governamentais complexos. Este projeto cria uma interface simples, rápida e esteticamente agradável para explorar esses dados sem nenhum cadastro ou chave de API.

---

## 🖼️ Interface

A aplicação possui:

- **Dropdown de estados** — carregado automaticamente da API IBGE com todos os 27 estados/DF
- **Campo de busca** — filtra municípios em tempo real por nome
- **Barra de estatísticas** — exibe total de municípios e quantidade filtrada
- **Grid de cards** — cada card mostra nome, código IBGE e microrregião do município
- **Tratamento de erros** — mensagens amigáveis quando a API IBGE não responde
- **Loading animado** — feedback visual durante as requisições

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Backend | Java 17 + Spring Boot 3.2 |
| HTTP Client | RestTemplate (Spring Web) |
| Template Engine | Thymeleaf |
| Frontend | HTML5 + CSS3 + JavaScript (Vanilla) |
| Fontes | Google Fonts (Syne + DM Sans) |
| Testes | JUnit 5 + WireMock 3.5 + MockMvc |
| Build | Maven 3.9+ |
| Container | Docker (multi-stage build) |
| CI/CD | GitHub Actions |
| Deploy | Render.com |
| API Externa | [IBGE Localidades](https://servicodados.ibge.gov.br/api/v1/localidades) |

---

## 🚀 Como Rodar Localmente

### Pré-requisitos

- **Java 17+** instalado ([baixar aqui](https://adoptium.net/))
- **Maven 3.9+** instalado ([baixar aqui](https://maven.apache.org/download.cgi))
- Conexão com a internet (para acessar a API do IBGE)

### Passo a passo

```bash
# 1. Clone o repositório
git clone https://github.com/SEU_USUARIO/municipios-br.git
cd municipios-br

# 2. Compile e inicie a aplicação
mvn spring-boot:run

# 3. Acesse no navegador
# http://localhost:8080
```

A aplicação estará disponível em **http://localhost:8080** 🎉

---

## 🧪 Como Rodar os Testes

Os testes de integração usam **WireMock** para mockar a API do IBGE (sem necessidade de internet durante os testes):

```bash
mvn test
```

Os 4 testes cobrem:
1. ✅ Listagem de estados com sucesso (API mockada)
2. ✅ Listagem de municípios de um estado (API mockada)
3. ✅ Comportamento quando a API retorna erro 500 → resposta 503 com mensagem amigável
4. ✅ Carregamento da página principal (HTTP 200)

---

## 🐳 Como Rodar com Docker

```bash
# 1. Build da imagem
docker build -t municipios-br .

# 2. Executar o container
docker run -p 8080:8080 municipios-br

# 3. Acessar em http://localhost:8080
```

---

## ☁️ Deploy no Render.com

### Pré-requisitos
- Conta gratuita no [Render.com](https://render.com)
- Repositório no GitHub com este código

### Passo a passo

1. **Acesse** [render.com](https://render.com) e faça login
2. No Dashboard, clique em **"New +"** → **"Web Service"**
3. Conecte sua conta GitHub (se ainda não conectou)
4. Selecione o repositório `municipios-br`
5. Configure o serviço:
   | Campo | Valor |
   |-------|-------|
   | Name | `municipios-br` |
   | Runtime | **Docker** |
   | Branch | `main` |
   | Dockerfile Path | `./Dockerfile` |
   | Instance Type | Free |
6. Clique em **"Create Web Service"**
7. Aguarde o build (~3-5 minutos)
8. Copie a URL gerada (ex: `https://municipios-br.onrender.com`) e cole no topo deste README

### Deploy automático

O Render detecta automaticamente novos pushes para a branch `main` e faz redeploy. Não é necessária configuração adicional.

> ⚠️ **Nota:** Serviços gratuitos no Render "adormecem" após 15 minutos de inatividade. O primeiro acesso após inatividade pode demorar ~30 segundos para "acordar".

---

## 🔗 API Utilizada

- **API de Localidades IBGE**
- URL base: `https://servicodados.ibge.gov.br/api/v1`
- 100% gratuita, sem autenticação, sem cadastro
- [Documentação oficial](https://servicodados.ibge.gov.br/api/docs/localidades)

Endpoints utilizados:
- `GET /localidades/estados?orderBy=nome` — lista todos os estados
- `GET /localidades/estados/{uf}/municipios?orderBy=nome` — lista municípios de uma UF

---

## 📋 Issues e Histórico

- Resolve a issue **[#1 — Painel interativo de consulta de municípios brasileiros via API IBGE](../../issues/1)**

---

## 📁 Estrutura do Projeto

```
municipios-br/
├── .github/
│   └── workflows/
│       └── ci.yml                    # CI com GitHub Actions
├── src/
│   ├── main/
│   │   ├── java/com/academico/municipiosbr/
│   │   │   ├── MunicipiosBrApplication.java   # Entry point
│   │   │   ├── AppConfig.java                 # Bean RestTemplate
│   │   │   ├── controller/
│   │   │   │   └── MunicipioController.java   # Rotas web e REST
│   │   │   ├── service/
│   │   │   │   └── IbgeService.java           # Lógica de negócio + HTTP
│   │   │   ├── model/
│   │   │   │   ├── Estado.java                # DTO estado
│   │   │   │   └── Municipio.java             # DTO município
│   │   │   └── exception/
│   │   │       └── IbgeApiException.java      # Exceção customizada
│   │   └── resources/
│   │       ├── templates/
│   │       │   └── index.html                 # Template Thymeleaf
│   │       ├── static/
│   │       │   ├── css/style.css              # Estilos
│   │       │   └── js/app.js                  # Lógica frontend
│   │       └── application.properties
│   └── test/
│       └── java/com/academico/municipiosbr/
│           └── IbgeIntegrationTest.java        # Testes com WireMock
├── Dockerfile
├── pom.xml
└── README.md
```

---

## 👨‍💻 Autor

Projeto acadêmico — entrega intermediária  
Java + Spring Boot · 2025
