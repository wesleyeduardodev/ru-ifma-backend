package br.edu.ifma.ru.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email deve ser valido")
    @Size(max = 150, message = "Email deve ter no maximo 150 caracteres")
    String email,

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 1, max = 72, message = "Senha deve ter no maximo 72 caracteres")
    String senha
) {}
