package com.academico.municipiosbr.controller;

import com.academico.municipiosbr.exception.IbgeApiException;
import com.academico.municipiosbr.model.Estado;
import com.academico.municipiosbr.model.Municipio;
import com.academico.municipiosbr.service.IbgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class MunicipioController {

    private final IbgeService ibgeService;

    public MunicipioController(IbgeService ibgeService) {
        this.ibgeService = ibgeService;
    }

    /**
     * Página inicial — carrega lista de estados para o dropdown.
     */
    @GetMapping("/")
    public String index(Model model) {
        try {
            List<Estado> estados = ibgeService.listarEstados();
            model.addAttribute("estados", estados);
        } catch (IbgeApiException e) {
            model.addAttribute("estados", Collections.emptyList());
            model.addAttribute("erro", e.getMessage());
        }
        return "index";
    }

    /**
     * Endpoint REST: retorna municípios de um estado em JSON (chamado pelo JS da página).
     */
    @GetMapping("/api/municipios/{uf}")
    @ResponseBody
    public ResponseEntity<?> getMunicipios(@PathVariable String uf) {
        try {
            List<Municipio> municipios = ibgeService.listarMunicipiosPorEstado(uf.toUpperCase());
            return ResponseEntity.ok(municipios);
        } catch (IbgeApiException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Endpoint REST: retorna lista de estados em JSON.
     */
    @GetMapping("/api/estados")
    @ResponseBody
    public ResponseEntity<?> getEstados() {
        try {
            List<Estado> estados = ibgeService.listarEstados();
            return ResponseEntity.ok(estados);
        } catch (IbgeApiException e) {
            return ResponseEntity.status(503)
                    .body(Map.of("erro", e.getMessage()));
        }
    }
}
