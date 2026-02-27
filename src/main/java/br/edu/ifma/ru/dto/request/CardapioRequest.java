package br.edu.ifma.ru.dto.request;

import br.edu.ifma.ru.entity.TipoRefeicao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CardapioRequest(
    @NotNull(message = "Data é obrigatória")
    LocalDate data,

    @NotNull(message = "Tipo de refeição é obrigatório")
    TipoRefeicao tipoRefeicao,

    @NotBlank(message = "Prato principal é obrigatório")
    String pratoPrincipal,

    @NotBlank(message = "Acompanhamento é obrigatório")
    String acompanhamento,

    @NotBlank(message = "Salada é obrigatória")
    String salada,

    @NotBlank(message = "Sobremesa é obrigatória")
    String sobremesa,

    @NotBlank(message = "Suco é obrigatório")
    String suco
) {}
