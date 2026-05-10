package dev.eltoncosta.eventos_api.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.eltoncosta.eventos_api.dto.ReservaRequest;
import dev.eltoncosta.eventos_api.dto.ReservaResponse;
import dev.eltoncosta.eventos_api.service.ReservaService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ReservaResponse> salvar(@RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.salvar(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscarPorId(@PathVariable Long id) {
        ReservaResponse response = reservaService.buscarPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponse> atualizar(@PathVariable Long id, @RequestBody ReservaRequest request) {
        ReservaResponse response = reservaService.atualizar(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        reservaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ReservaResponse>> listar(
            @PageableDefault(size = 10, sort = "dataReservadaInicio", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<ReservaResponse> response = reservaService.listar(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/periodo")
    public ResponseEntity<Page<ReservaResponse>> listarPorPeriodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim,
            @PageableDefault(size = 10, sort = "dataReservadaInicio", direction = Sort.Direction.ASC)
            Pageable pageable) {
        Page<ReservaResponse> response = reservaService.listarPorPeriodo(inicio, fim, pageable);
        return ResponseEntity.ok(response);
    }

}
