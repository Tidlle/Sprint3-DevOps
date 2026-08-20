package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.VeterinarioRequest;
import com.clyvo.vitalpet.dto.VeterinarioResponse;
import com.clyvo.vitalpet.service.VeterinarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Veterinários")
@RestController
@RequestMapping("/api/veterinarios")
public class VeterinarioController {

    private final VeterinarioService veterinarioService;

    public VeterinarioController(VeterinarioService veterinarioService) {
        this.veterinarioService = veterinarioService;
    }

    @PostMapping
    public ResponseEntity<VeterinarioResponse> criar(@RequestBody @Valid VeterinarioRequest request) {
        VeterinarioResponse response = veterinarioService.criar(request);
        return ResponseEntity.created(URI.create("/api/veterinarios/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<VeterinarioResponse>> listar(@RequestParam(required = false) String nome,
                                                            @RequestParam(required = false) String especialidade,
                                                            @RequestParam(required = false) Long clinicaId,
                                                            @RequestParam(required = false) Boolean ativo,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "nome") String sortBy,
                                                            @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(veterinarioService.listar(nome, especialidade, clinicaId, ativo, pageable(page, size, sortBy, direction)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinarioResponse> atualizar(@PathVariable Long id, @RequestBody @Valid VeterinarioRequest request) {
        return ResponseEntity.ok(veterinarioService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        veterinarioService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<VeterinarioResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(veterinarioService.ativar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
