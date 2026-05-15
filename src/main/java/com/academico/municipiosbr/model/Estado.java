package com.academico.municipiosbr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Estado {

    private Long id;
    private String sigla;
    private String nome;
    private Regiao regiao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSigla() { return sigla; }
    public void setSigla(String sigla) { this.sigla = sigla; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Regiao getRegiao() { return regiao; }
    public void setRegiao(Regiao regiao) { this.regiao = regiao; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Regiao {
        private String nome;
        private String sigla;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getSigla() { return sigla; }
        public void setSigla(String sigla) { this.sigla = sigla; }
    }
}
