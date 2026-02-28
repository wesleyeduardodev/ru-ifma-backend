package br.edu.ifma.ru.service;

import br.edu.ifma.ru.dto.request.AdminRequest;
import br.edu.ifma.ru.dto.response.AdminResponse;
import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.exception.ResourceNotFoundException;
import br.edu.ifma.ru.repository.AdministradorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdministradorService {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<AdminResponse> listar() {
        return repository.findAll().stream()
                .map(AdminResponse::fromEntity)
                .toList();
    }

    public AdminResponse buscarPorId(Long id) {
        Administrador admin = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));
        return AdminResponse.fromEntity(admin);
    }

    public AdminResponse criar(AdminRequest request) {
        Administrador admin = new Administrador();
        admin.setNome(request.nome());
        admin.setEmail(request.email());
        admin.setSenha(passwordEncoder.encode(request.senha()));
        Administrador salvo = repository.save(admin);
        return AdminResponse.fromEntity(salvo);
    }

    public AdminResponse atualizar(Long id, AdminRequest request) {
        Administrador admin = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));
        admin.setNome(request.nome());
        admin.setEmail(request.email());
        admin.setSenha(passwordEncoder.encode(request.senha()));
        Administrador salvo = repository.save(admin);
        return AdminResponse.fromEntity(salvo);
    }

    private static final String EMAIL_ADMIN_PRINCIPAL = "admin@ifma.edu.br";

    public void deletar(Long id) {
        Administrador admin = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador nao encontrado"));

        if (EMAIL_ADMIN_PRINCIPAL.equals(admin.getEmail())) {
            throw new IllegalStateException("O administrador principal nao pode ser excluido");
        }

        if (repository.count() <= 1) {
            throw new IllegalStateException("O sistema deve ter pelo menos um administrador");
        }

        repository.deleteById(id);
    }
}
