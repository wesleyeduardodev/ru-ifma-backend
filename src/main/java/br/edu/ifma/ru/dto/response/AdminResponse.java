package br.edu.ifma.ru.dto.response;

import br.edu.ifma.ru.entity.Administrador;

import java.time.LocalDateTime;

public record AdminResponse(
    Long id,
    String nome,
    String email,
    LocalDateTime criadoEm
) {
    public static AdminResponse fromEntity(Administrador admin) {
        return new AdminResponse(
            admin.getId(),
            admin.getNome(),
            admin.getEmail(),
            admin.getCriadoEm()
        );
    }
}
