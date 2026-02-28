package br.edu.ifma.ru.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AlterarSenhaRequest(
    @NotBlank(message = "Senha atual e obrigatoria")
    @Size(max = 72, message = "Senha deve ter no maximo 72 caracteres")
    String senhaAtual,

    @NotBlank(message = "Nova senha e obrigatoria")
    @Size(min = 8, max = 72, message = "Nova senha deve ter entre 8 e 72 caracteres")
    String novaSenha
) {}
