package br.edu.ifma.ru.dto.request;

import br.edu.ifma.ru.entity.TipoRefeicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CardapioRequest(
    @NotNull(message = "Data e obrigatoria")
    LocalDate data,

    @NotNull(message = "Tipo de refeicao e obrigatorio")
    TipoRefeicao tipoRefeicao,

    @NotBlank(message = "Prato principal e obrigatorio")
    @Size(max = 200, message = "Prato principal deve ter no maximo 200 caracteres")
    String pratoPrincipal,

    @NotBlank(message = "Acompanhamento e obrigatorio")
    @Size(max = 200, message = "Acompanhamento deve ter no maximo 200 caracteres")
    String acompanhamento,

    @NotBlank(message = "Salada e obrigatoria")
    @Size(max = 200, message = "Salada deve ter no maximo 200 caracteres")
    String salada,

    @NotBlank(message = "Sobremesa e obrigatoria")
    @Size(max = 200, message = "Sobremesa deve ter no maximo 200 caracteres")
    String sobremesa,

    @NotBlank(message = "Suco e obrigatorio")
    @Size(max = 200, message = "Suco deve ter no maximo 200 caracteres")
    String suco
) {}
