package br.edu.ifma.ru.service;

import br.edu.ifma.ru.dto.request.AlterarSenhaRequest;
import br.edu.ifma.ru.dto.request.LoginRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.dto.response.LoginResponse;
import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.exception.ResourceNotFoundException;
import br.edu.ifma.ru.repository.AdministradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse autenticar(LoginRequest request) {
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

        return new LoginResponse("Login realizado com sucesso", AdminResponse.fromEntity(admin));
    }

    public AdminResponse buscarPorEmail(String email) {
        Administrador admin = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));
        return AdminResponse.fromEntity(admin);
    }

    public void alterarSenha(String email, AlterarSenhaRequest request) {
        Administrador admin = repository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));

        if (!passwordEncoder.matches(request.senhaAtual(), admin.getSenha())) {
            throw new IllegalStateException("Senha atual incorreta");
        }

        admin.setSenha(passwordEncoder.encode(request.novaSenha()));
        repository.save(admin);
    }
}
