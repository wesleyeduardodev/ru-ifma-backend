package br.edu.ifma.ru.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdminRequest(
    @NotBlank(message = "Nome é obrigatório")
    String nome,

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email,

    @NotBlank(message = "Senha é obrigatória")
    String senha
) {}
