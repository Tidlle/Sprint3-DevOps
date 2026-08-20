package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.TutorRequest;
import com.clyvo.vitalpet.dto.TutorResponse;
import com.clyvo.vitalpet.service.TutorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Tutores")
@RestController
@RequestMapping("/api/tutores")
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping
    public ResponseEntity<TutorResponse> criar(@RequestBody @Valid TutorRequest request) {
        TutorResponse response = tutorService.criar(request);
        return ResponseEntity.created(URI.create("/api/tutores/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TutorResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<TutorResponse>> listar(@RequestParam(required = false) String nome,
                                                      @RequestParam(required = false) String cpf,
                                                      @RequestParam(required = false) String cidade,
                                                      @RequestParam(required = false) Boolean ativo,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @RequestParam(defaultValue = "nome") String sortBy,
                                                      @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(tutorService.listar(nome, cpf, cidade, ativo, pageable(page, size, sortBy, direction)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TutorResponse> atualizar(@PathVariable Long id, @RequestBody @Valid TutorRequest request) {
        return ResponseEntity.ok(tutorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        tutorService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<TutorResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(tutorService.ativar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
