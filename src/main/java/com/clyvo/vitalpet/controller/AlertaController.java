package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.AlertaRequest;
import com.clyvo.vitalpet.dto.AlertaResponse;
import com.clyvo.vitalpet.service.AlertaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Alertas")
@RestController
@RequestMapping("/api/alertas")
public class AlertaController {

    private final AlertaService alertaService;

    public AlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @PostMapping
    public ResponseEntity<AlertaResponse> criar(@RequestBody @Valid AlertaRequest request) {
        AlertaResponse response = alertaService.criar(request);
        return ResponseEntity.created(URI.create("/api/alertas/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<AlertaResponse>> listar(@RequestParam(required = false) Long petId,
                                                       @RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String prioridade,
                                                       @RequestParam(required = false) String tipo,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "dataAlerta") String sortBy,
                                                       @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(alertaService.listar(petId, status, prioridade, tipo, pageable(page, size, sortBy, direction)));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<AlertaResponse> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.resolver(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AlertaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(alertaService.cancelar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
