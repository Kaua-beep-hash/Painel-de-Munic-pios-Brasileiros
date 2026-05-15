# PULL_REQUEST.md — Texto completo do Pull Request

---

## Título
`[Entrega Intermediária] Painel de Municípios Brasileiros — Integração API IBGE`

---

## Descrição

Este PR implementa a entrega intermediária do projeto acadêmico: uma aplicação web completa em **Java + Spring Boot** que consome a **API pública de Localidades do IBGE** para exibir dados de municípios brasileiros em uma interface web moderna e responsiva.

### O que foi feito

- ✅ Estrutura completa do projeto Spring Boot com separação em camadas (Controller → Service → Model)
- ✅ Integração com a API IBGE (`/localidades/estados` e `/localidades/estados/{uf}/municipios`) via `RestTemplate`
- ✅ Interface web com Thymeleaf + CSS/JS customizados (design editorial, tipografia Syne + DM Sans)
- ✅ Funcionalidades:
  - Dropdown carregado dinamicamente com os 27 estados + DF
  - Grid de municípios com cards exibindo nome, código IBGE e microrregião
  - Filtro em tempo real por nome do município
  - Barra de estatísticas (total / filtrados / estado selecionado)
  - Loading animado e mensagens de erro amigáveis
- ✅ Tratamento de exceções com `IbgeApiException` e respostas HTTP adequadas (503)
- ✅ **4 testes de integração** com WireMock mockando a API IBGE (incluindo cenário de falha)
- ✅ Dockerfile multi-stage para build e execução em container Docker
- ✅ CI/CD via GitHub Actions (`.github/workflows/ci.yml`) rodando em push/PR para `main`
- ✅ README.md completo com badge de CI, instruções de setup local e passo a passo de deploy no Render.com

### Arquitetura

```
MunicipioController (Spring MVC)
       ↓
IbgeService (RestTemplate → API IBGE)
       ↓
Modelos: Estado, Municipio (DTOs com @JsonIgnoreProperties)
       ↓
index.html (Thymeleaf) + app.js (fetch API) + style.css
```

### Testes

```bash
mvn test
# IbgeIntegrationTest > deveRetornarEstadosQuandoApiIbgeRespondeComSucesso  ✅ PASSED
# IbgeIntegrationTest > deveRetornarMunicipiosDeSpQuandoApiRespondeComSucesso ✅ PASSED
# IbgeIntegrationTest > deveRetornar503QuandoApiIbgeFalha                    ✅ PASSED
# IbgeIntegrationTest > paginaPrincipalDeveRetornarOk                        ✅ PASSED
```

---

## Closes

closes #1

---

## Checklist de Entrega

- [x] GitHub Issue criada (#1) com título, descrição e critérios de aceitação
- [x] Estrutura de projeto Spring Boot gerada (Maven, Java 17, Spring Web, Thymeleaf)
- [x] Integração com API pública gratuita (IBGE — sem autenticação)
- [x] RestTemplate implementado no `IbgeService`
- [x] Interface web visualmente apresentável e responsiva
- [x] Tratamento de erros com mensagens amigáveis ao usuário
- [x] Testes de integração com WireMock (`mvn test` passando)
- [x] Dockerfile configurado (multi-stage build)
- [x] Instruções de deploy no Render.com no README
- [x] GitHub Actions CI configurado (`.github/workflows/ci.yml`)
- [x] README.md completo com badge de CI e link de deploy
- [x] Código organizado em camadas: Controller, Service, Model, Exception

---
---

# DEPLOY_RENDER.md — Passo a Passo Completo de Deploy no Render.com

## Pré-requisitos

- Conta gratuita no **GitHub** com o repositório deste projeto
- Conta gratuita no **Render.com** (cadastre-se em https://render.com com sua conta GitHub)

---

## Passo 1 — Preparar o repositório GitHub

```bash
# Na raiz do projeto, inicialize o Git se ainda não tiver:
git init
git add .
git commit -m "feat: entrega intermediária — painel municípios BR"

# Crie um repositório no GitHub (via interface web ou GitHub CLI):
gh repo create municipios-br --public --push --source=.

# Ou manualmente:
git remote add origin https://github.com/SEU_USUARIO/municipios-br.git
git branch -M main
git push -u origin main
```

---

## Passo 2 — Criar o Web Service no Render

1. Acesse **https://render.com** e faça login com sua conta GitHub
2. No Dashboard, clique no botão **"New +"** (canto superior direito)
3. Selecione **"Web Service"**
4. Na tela "Connect a repository", clique em **"Connect account"** (se ainda não conectou o GitHub)
5. Selecione o repositório **`municipios-br`**
6. Clique em **"Connect"**

---

## Passo 3 — Configurar o serviço

Preencha os campos conforme abaixo:

| Campo | Valor |
|-------|-------|
| **Name** | `municipios-br` |
| **Region** | Oregon (US West) — gratuito |
| **Branch** | `main` |
| **Runtime** | **Docker** ← _selecione Docker_ |
| **Dockerfile Path** | `./Dockerfile` |
| **Instance Type** | **Free** |

> ⚠️ Certifique-se de selecionar **"Docker"** como Runtime, não "Node" ou "Python"

Não é necessário configurar variáveis de ambiente para a versão básica (a API do IBGE não exige autenticação).

---

## Passo 4 — Deploy inicial

1. Clique em **"Create Web Service"**
2. O Render iniciará automaticamente o build do Dockerfile
3. Acompanhe os logs em tempo real na aba **"Logs"**
4. Aguarde a mensagem: `==> Your service is live 🎉`
5. A URL do serviço aparecerá no topo da página, no formato:
   ```
   https://municipios-br.onrender.com
   ```

Tempo estimado do primeiro deploy: **3 a 6 minutos**

---

## Passo 5 — Atualizar o README com o link

```bash
# Edite o README.md e substitua o placeholder pelo link real:
# [INSERIR LINK APÓS DEPLOY] → https://municipios-br.onrender.com

git add README.md
git commit -m "docs: adiciona link do deploy no Render"
git push
```

---

## Passo 6 — Deploy automático (CI/CD)

O Render já configura **auto-deploy** por padrão: toda vez que você fizer `git push` para a branch `main`, o Render detectará automaticamente e fará um novo deploy.

Para verificar/configurar:
- Acesse seu serviço no Render Dashboard
- Aba **"Settings"** → seção **"Build & Deploy"**
- Confirme que **"Auto-Deploy"** está marcado como **"Yes"**

---

## Solução de Problemas Comuns

| Problema | Solução |
|---------|---------|
| Build falha com "mvn not found" | O Dockerfile usa `eclipse-temurin:17-jdk-alpine` que não inclui Maven. Use a linha `RUN mvn package` e garanta que o Maven está no PATH da imagem de build |
| Aplicação não inicia | Verifique os logs do Render → a porta deve ser 8080 e o Render a mapeará automaticamente |
| "Service Unavailable" após longo período | Normal no plano gratuito — o serviço "adormece" após 15min sem uso. O próximo acesso levará ~30s |
| API IBGE não responde | Verifique a URL em `application.properties`: `ibge.api.base-url=https://servicodados.ibge.gov.br/api/v1` |
