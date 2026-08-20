package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.AcompanhamentoRequest;
import com.clyvo.vitalpet.dto.AcompanhamentoResponse;
import com.clyvo.vitalpet.service.AcompanhamentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Acompanhamentos")
@RestController
@RequestMapping("/api/acompanhamentos")
public class AcompanhamentoController {

    private final AcompanhamentoService acompanhamentoService;

    public AcompanhamentoController(AcompanhamentoService acompanhamentoService) {
        this.acompanhamentoService = acompanhamentoService;
    }

    @PostMapping
    public ResponseEntity<AcompanhamentoResponse> criar(@RequestBody @Valid AcompanhamentoRequest request) {
        AcompanhamentoResponse response = acompanhamentoService.criar(request);
        return ResponseEntity.created(URI.create("/api/acompanhamentos/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcompanhamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(acompanhamentoService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<AcompanhamentoResponse>> listar(@RequestParam(required = false) Long consultaId,
                                                               @RequestParam(required = false) Long petId,
                                                               @RequestParam(required = false) String status,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size,
                                                               @RequestParam(defaultValue = "dataInicio") String sortBy,
                                                               @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(acompanhamentoService.listar(consultaId, petId, status, pageable(page, size, sortBy, direction)));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<AcompanhamentoResponse> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(acompanhamentoService.concluir(id));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<AcompanhamentoResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(acompanhamentoService.cancelar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
