package com.academico.municipiosbr.service;

import com.academico.municipiosbr.exception.IbgeApiException;
import com.academico.municipiosbr.model.Estado;
import com.academico.municipiosbr.model.Municipio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class IbgeService {

    private final RestTemplate restTemplate;

    @Value("${ibge.api.base-url:https://servicodados.ibge.gov.br/api/v1}")
    private String ibgeBaseUrl;

    public IbgeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Retorna todos os estados do Brasil, ordenados por nome.
     */
    public List<Estado> listarEstados() {
        String url = ibgeBaseUrl + "/localidades/estados?orderBy=nome";
        try {
            ResponseEntity<Estado[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Estado[].class
            );
            Estado[] estados = response.getBody();
            if (estados == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(estados);
        } catch (RestClientException e) {
            throw new IbgeApiException(
                    "Não foi possível conectar à API do IBGE. Tente novamente em instantes.", e);
        }
    }

    /**
     * Retorna todos os municípios de um estado (por sigla da UF), ordenados por nome.
     */
    public List<Municipio> listarMunicipiosPorEstado(String uf) {
        String url = ibgeBaseUrl + "/localidades/estados/" + uf + "/municipios?orderBy=nome";
        try {
            ResponseEntity<Municipio[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Municipio[].class
            );
            Municipio[] municipios = response.getBody();
            if (municipios == null) {
                return Collections.emptyList();
            }
            List<Municipio> lista = Arrays.asList(municipios);
            lista.sort(Comparator.comparing(Municipio::getNome));
            return lista;
        } catch (RestClientException e) {
            throw new IbgeApiException(
                    "Erro ao buscar municípios do estado '" + uf + "'. Verifique a sigla e tente novamente.", e);
        }
    }
}
