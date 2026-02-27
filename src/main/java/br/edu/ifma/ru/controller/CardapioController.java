package br.edu.ifma.ru.controller;

import br.edu.ifma.ru.dto.request.CardapioRequest;
import br.edu.ifma.ru.dto.response.CardapioResponse;
import br.edu.ifma.ru.service.CardapioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cardapios")
public class CardapioController {

    private final CardapioService service;

    public CardapioController(CardapioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CardapioResponse>> listarPorData(@RequestParam LocalDate data) {
        return ResponseEntity.ok(service.listarPorData(data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardapioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<CardapioResponse> criar(@Valid @RequestBody CardapioRequest request) {
        CardapioResponse response = service.criar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardapioResponse> atualizar(@PathVariable Long id, @Valid @RequestBody CardapioRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
