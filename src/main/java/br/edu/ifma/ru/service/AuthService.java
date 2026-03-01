package br.edu.ifma.ru.service;

import br.edu.ifma.ru.dto.request.AlterarSenhaRequest;
import br.edu.ifma.ru.dto.request.LoginRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.entity.RefreshToken;
import br.edu.ifma.ru.exception.ResourceNotFoundException;
import br.edu.ifma.ru.repository.AdministradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(AdministradorRepository repository,
                       PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public record TokensGerados(String accessToken, String refreshToken, AdminResponse admin) {}

    public TokensGerados autenticar(LoginRequest request) {
        Optional<Administrador> optAdmin = repository.findByEmail(request.email());
        String senhaFalsa = "$2a$12$invalidsaltinvalidhashforrandomtimingprotection";

        if (optAdmin.isEmpty()) {
            passwordEncoder.matches(request.senha(), senhaFalsa);
            return null;
        }

        Administrador admin = optAdmin.get();
        if (!passwordEncoder.matches(request.senha(), admin.getSenha())) {
            return null;
        }

        String accessToken = tokenService.gerarAccessToken(admin.getEmail());
        String refreshToken = tokenService.gerarRefreshToken(admin.getId());

        return new TokensGerados(accessToken, refreshToken, AdminResponse.fromEntity(admin));
    }

    @Transactional
    public TokensGerados renovarTokens(String refreshToken) {
        Optional<RefreshToken> optToken = tokenService.validarRefreshToken(refreshToken);

        if (optToken.isEmpty()) {
            return null;
        }

        RefreshToken token = optToken.get();
        tokenService.invalidarRefreshToken(refreshToken);

        Administrador admin = repository.findById(token.getAdministradorId())
                .orElse(null);

        if (admin == null) {
            return null;
        }

        String novoAccessToken = tokenService.gerarAccessToken(admin.getEmail());
        String novoRefreshToken = tokenService.gerarRefreshToken(admin.getId());

        return new TokensGerados(novoAccessToken, novoRefreshToken, AdminResponse.fromEntity(admin));
    }

    @Transactional
    public void logout(String refreshToken) {
        tokenService.invalidarRefreshToken(refreshToken);
    }

    public AdminResponse buscarPorEmail(String email) {
        Administrador admin = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));
        return AdminResponse.fromEntity(admin);
    }

    @Transactional
    public void alterarSenha(String email, AlterarSenhaRequest request) {
        Administrador admin = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));

        if (!passwordEncoder.matches(request.senhaAtual(), admin.getSenha())) {
            throw new IllegalStateException("Senha atual incorreta");
        }

        admin.setSenha(passwordEncoder.encode(request.novaSenha()));
        repository.save(admin);

        tokenService.invalidarTodosTokens(admin.getId());
    }
}
