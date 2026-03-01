package br.edu.ifma.ru.controller;

import br.edu.ifma.ru.dto.request.AlterarSenhaRequest;
import br.edu.ifma.ru.dto.request.LoginRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.dto.response.LoginResponse;
import br.edu.ifma.ru.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final boolean cookieSecure;
    private static final String REFRESH_COOKIE_NAME = "refresh_token";
    private static final int REFRESH_COOKIE_MAX_AGE = 604800;

    public AuthController(AuthService authService,
                          @Value("${jwt.cookie-secure}") boolean cookieSecure) {
        this.authService = authService;
        this.cookieSecure = cookieSecure;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request,
                                   HttpServletResponse response) {
        AuthService.TokensGerados tokens = authService.autenticar(request);

        if (tokens == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "Credenciais inválidas"));
        }

        adicionarCookieRefresh(response, tokens.refreshToken());
        return ResponseEntity.ok(new LoginResponse(tokens.accessToken(), tokens.admin()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extrairCookieRefresh(request);

        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "Refresh token ausente"));
        }

        AuthService.TokensGerados tokens = authService.renovarTokens(refreshToken);

        if (tokens == null) {
            removerCookieRefresh(response);
            return ResponseEntity.status(401)
                    .body(Map.of("erro", "Refresh token inválido ou expirado"));
        }

        adicionarCookieRefresh(response, tokens.refreshToken());
        return ResponseEntity.ok(new LoginResponse(tokens.accessToken(), tokens.admin()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extrairCookieRefresh(request);

        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        removerCookieRefresh(response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AdminResponse> me(Authentication authentication) {
        AdminResponse admin = authService.buscarPorEmail(authentication.getName());
        return ResponseEntity.ok(admin);
    }

    @PutMapping("/alterar-senha")
    public ResponseEntity<Void> alterarSenha(@Valid @RequestBody AlterarSenhaRequest request,
                                             Authentication authentication) {
        authService.alterarSenha(authentication.getName(), request);
        return ResponseEntity.noContent().build();
    }

    private void adicionarCookieRefresh(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth/");
        cookie.setMaxAge(REFRESH_COOKIE_MAX_AGE);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private void removerCookieRefresh(HttpServletResponse response) {
        Cookie cookie = new Cookie(REFRESH_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/api/auth/");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    private String extrairCookieRefresh(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
