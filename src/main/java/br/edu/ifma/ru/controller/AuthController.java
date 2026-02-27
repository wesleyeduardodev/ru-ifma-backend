package br.edu.ifma.ru.controller;

import br.edu.ifma.ru.dto.request.LoginRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.dto.response.LoginResponse;
import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.repository.AdministradorRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Administrador admin = repository.findByEmail(request.email())
                .orElse(null);

        if (admin == null || !passwordEncoder.matches(request.senha(), admin.getSenha())) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Credenciais inválidas", null));
        }

        return ResponseEntity.ok(new LoginResponse("Login realizado com sucesso", AdminResponse.fromEntity(admin)));
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponse> me(Authentication authentication) {
        String email = authentication.getName();
        Administrador admin = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"));
        return ResponseEntity.ok(AdminResponse.fromEntity(admin));
    }
}
