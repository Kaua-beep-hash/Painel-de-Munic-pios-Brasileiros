package com.academico.municipiosbr;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class IbgeIntegrationTest {

    static WireMockServer wireMockServer;

    @Autowired
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Sobrescreve a URL base da API IBGE para apontar ao WireMock
        // O valor é definido dinamicamente após o servidor iniciar
        registry.add("ibge.api.base-url",
                () -> wireMockServer == null ? "http://localhost:9999" :
                      "http://localhost:" + wireMockServer.port());
    }

    // ---------------------------------------------------------------
    // Teste 1: listar estados com sucesso
    // ---------------------------------------------------------------
    @Test
    @DisplayName("GET /api/estados → deve retornar lista de estados mockada pelo WireMock")
    void deveRetornarEstadosQuandoApiIbgeRespondeComSucesso() throws Exception {

        String jsonEstados = """
            [
              {
                "id": 35,
                "sigla": "SP",
                "nome": "São Paulo",
                "regiao": { "id": 3, "sigla": "SE", "nome": "Sudeste" }
              },
              {
                "id": 33,
                "sigla": "RJ",
                "nome": "Rio de Janeiro",
                "regiao": { "id": 3, "sigla": "SE", "nome": "Sudeste" }
              }
            ]
            """;

        wireMockServer.stubFor(
                get(urlPathEqualTo("/localidades/estados"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(jsonEstados))
        );

        mockMvc.perform(get("/api/estados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].sigla", is("SP")))
                .andExpect(jsonPath("$[0].nome", is("São Paulo")))
                .andExpect(jsonPath("$[1].sigla", is("RJ")));
    }

    // ---------------------------------------------------------------
    // Teste 2: listar municípios de SP com sucesso
    // ---------------------------------------------------------------
    @Test
    @DisplayName("GET /api/municipios/SP → deve retornar lista de municípios mockada")
    void deveRetornarMunicipiosDeSpQuandoApiRespondeComSucesso() throws Exception {

        String jsonMunicipios = """
            [
              {
                "id": 3550308,
                "nome": "São Paulo",
                "microrregiao": {
                  "id": 35061,
                  "nome": "São Paulo",
                  "mesorregiao": { "id": 3515, "nome": "Metropolitana de São Paulo" }
                }
              },
              {
                "id": 3509502,
                "nome": "Campinas",
                "microrregiao": {
                  "id": 35036,
                  "nome": "Campinas",
                  "mesorregiao": { "id": 3508, "nome": "Campinas" }
                }
              }
            ]
            """;

        wireMockServer.stubFor(
                get(urlPathEqualTo("/localidades/estados/SP/municipios"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(jsonMunicipios))
        );

        mockMvc.perform(get("/api/municipios/SP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nome", is("Campinas")))  // ordenado por nome
                .andExpect(jsonPath("$[1].nome", is("São Paulo")));
    }

    // ---------------------------------------------------------------
    // Teste 3: comportamento quando a API IBGE retorna erro 500
    // ---------------------------------------------------------------
    @Test
    @DisplayName("GET /api/municipios/XX → deve retornar 503 com mensagem amigável quando API falha")
    void deveRetornar503QuandoApiIbgeFalha() throws Exception {

        wireMockServer.stubFor(
                get(urlPathEqualTo("/localidades/estados/XX/municipios"))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withBody("Internal Server Error"))
        );

        mockMvc.perform(get("/api/municipios/XX"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.erro", containsString("Erro ao buscar municípios")));
    }

    // ---------------------------------------------------------------
    // Teste 4: página principal carrega sem erros
    // ---------------------------------------------------------------
    @Test
    @DisplayName("GET / → página principal deve retornar HTTP 200")
    void paginaPrincipalDeveRetornarOk() throws Exception {

        String jsonEstados = """
            [
              { "id": 35, "sigla": "SP", "nome": "São Paulo",
                "regiao": { "sigla": "SE", "nome": "Sudeste" } }
            ]
            """;

        wireMockServer.stubFor(
                get(urlPathEqualTo("/localidades/estados"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(jsonEstados))
        );

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }
}
