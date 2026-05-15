package com.academico.municipiosbr.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Municipio {

    private Long id;
    private String nome;
    private Microrregiao microrregiao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Microrregiao getMicrorregiao() { return microrregiao; }
    public void setMicrorregiao(Microrregiao microrregiao) { this.microrregiao = microrregiao; }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Microrregiao {
        private String nome;
        private Mesorregiao mesorregiao;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public Mesorregiao getMesorregiao() { return mesorregiao; }
        public void setMesorregiao(Mesorregiao mesorregiao) { this.mesorregiao = mesorregiao; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Mesorregiao {
        private String nome;

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
    }
}
