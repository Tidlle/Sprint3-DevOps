package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.ConsultaAtualizacaoRequest;
import com.clyvo.vitalpet.dto.ConsultaRequest;
import com.clyvo.vitalpet.dto.ConsultaResponse;
import com.clyvo.vitalpet.dto.FinalizarConsultaRequest;
import com.clyvo.vitalpet.service.ConsultaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@Tag(name = "Consultas")
@RestController
@RequestMapping("/api/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PostMapping
    public ResponseEntity<ConsultaResponse> criar(@RequestBody @Valid ConsultaRequest request) {
        ConsultaResponse response = consultaService.criar(request);
        return ResponseEntity.created(URI.create("/api/consultas/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<ConsultaResponse>> listar(@RequestParam(required = false) Long petId,
                                                         @RequestParam(required = false) Long veterinarioId,
                                                         @RequestParam(required = false) String status,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size,
                                                         @RequestParam(defaultValue = "dataHora") String sortBy,
                                                         @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(consultaService.listar(petId, veterinarioId, status, inicio, fim, pageable(page, size, sortBy, direction)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ConsultaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid ConsultaAtualizacaoRequest request) {
        return ResponseEntity.ok(consultaService.atualizar(id, request));
    }

    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<ConsultaResponse> finalizar(@PathVariable Long id, @RequestBody @Valid FinalizarConsultaRequest request) {
        return ResponseEntity.ok(consultaService.finalizar(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<ConsultaResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(consultaService.cancelar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
