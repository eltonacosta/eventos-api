package dev.eltoncosta.eventos_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.eltoncosta.eventos_api.dto.AmbienteRequest;
import dev.eltoncosta.eventos_api.dto.AmbienteResponse;
import dev.eltoncosta.eventos_api.service.AmbienteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ambientes")
@RequiredArgsConstructor
public class AmbienteController {

    private final AmbienteService ambienteService;

    @PostMapping
    public ResponseEntity<AmbienteResponse> salvar(@RequestBody AmbienteRequest request) {
        AmbienteResponse response = ambienteService.salvar(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AmbienteResponse> buscarPorId(@PathVariable Long id) {
        AmbienteResponse response = ambienteService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        ambienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<AmbienteResponse>> listar(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<AmbienteResponse> response = ambienteService.listar(pageable);
        return ResponseEntity.ok(response);
    }
}
