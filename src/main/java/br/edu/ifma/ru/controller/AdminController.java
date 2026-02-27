package br.edu.ifma.ru.controller;

import br.edu.ifma.ru.dto.request.AdminRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.service.AdministradorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdministradorService service;

    public AdminController(AdministradorService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AdminResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AdminResponse> criar(@Valid @RequestBody AdminRequest request) {
        AdminResponse response = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminResponse> atualizar(@PathVariable Long id, @Valid @RequestBody AdminRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
