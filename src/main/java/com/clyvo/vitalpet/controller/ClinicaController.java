package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.ClinicaRequest;
import com.clyvo.vitalpet.dto.ClinicaResponse;
import com.clyvo.vitalpet.service.ClinicaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Clínicas")
@RestController
@RequestMapping("/api/clinicas")
public class ClinicaController {

    private final ClinicaService clinicaService;

    public ClinicaController(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    @PostMapping
    public ResponseEntity<ClinicaResponse> criar(@RequestBody @Valid ClinicaRequest request) {
        ClinicaResponse response = clinicaService.criar(request);
        return ResponseEntity.created(URI.create("/api/clinicas/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClinicaResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clinicaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<ClinicaResponse>> listar(@RequestParam(required = false) String nome,
                                                        @RequestParam(required = false) String cidade,
                                                        @RequestParam(required = false) String estado,
                                                        @RequestParam(required = false) Boolean ativa,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "nome") String sortBy,
                                                        @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(clinicaService.listar(nome, cidade, estado, ativa, pageable(page, size, sortBy, direction)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClinicaResponse> atualizar(@PathVariable Long id, @RequestBody @Valid ClinicaRequest request) {
        return ResponseEntity.ok(clinicaService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        clinicaService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ClinicaResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(clinicaService.ativar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
