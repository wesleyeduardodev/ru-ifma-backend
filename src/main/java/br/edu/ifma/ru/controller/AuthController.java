package br.edu.ifma.ru.controller;

import br.edu.ifma.ru.dto.request.LoginRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.dto.response.LoginResponse;
import br.edu.ifma.ru.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.autenticar(request);

        if (response == null) {
            return ResponseEntity.status(401)
                    .body(new LoginResponse("Credenciais invalidas", null));
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponse> me(Authentication authentication) {
        AdminResponse admin = authService.buscarPorEmail(authentication.getName());
        return ResponseEntity.ok(admin);
    }
}
