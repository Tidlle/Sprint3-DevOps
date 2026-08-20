package com.clyvo.vitalpet.controller;

import com.clyvo.vitalpet.dto.PetRequest;
import com.clyvo.vitalpet.dto.PetResponse;
import com.clyvo.vitalpet.service.PetService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Pets")
@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<PetResponse> criar(@RequestBody @Valid PetRequest request) {
        PetResponse response = petService.criar(request);
        return ResponseEntity.created(URI.create("/api/pets/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<Page<PetResponse>> listar(@RequestParam(required = false) String nome,
                                                    @RequestParam(required = false) String especie,
                                                    @RequestParam(required = false) Long tutorId,
                                                    @RequestParam(required = false) Boolean ativo,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "nome") String sortBy,
                                                    @RequestParam(defaultValue = "asc") String direction) {
        return ResponseEntity.ok(petService.listar(nome, especie, tutorId, ativo, pageable(page, size, sortBy, direction)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponse> atualizar(@PathVariable Long id, @RequestBody @Valid PetRequest request) {
        return ResponseEntity.ok(petService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        petService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<PetResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(petService.ativar(id));
    }

    private Pageable pageable(int page, int size, String sortBy, String direction) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(Math.max(page, 0), safeSize, Sort.by(dir, sortBy));
    }
}
