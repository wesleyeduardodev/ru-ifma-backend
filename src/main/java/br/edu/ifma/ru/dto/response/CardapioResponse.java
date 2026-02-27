package br.edu.ifma.ru.dto.response;

import br.edu.ifma.ru.entity.Cardapio;
import br.edu.ifma.ru.entity.TipoRefeicao;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record CardapioResponse(
    Long id,
    LocalDate data,
    TipoRefeicao tipoRefeicao,
    String pratoPrincipal,
    String acompanhamento,
    String salada,
    String sobremesa,
    String suco,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {
    public static CardapioResponse fromEntity(Cardapio cardapio) {
        return new CardapioResponse(
            cardapio.getId(),
            cardapio.getData(),
            cardapio.getTipoRefeicao(),
            cardapio.getPratoPrincipal(),
            cardapio.getAcompanhamento(),
            cardapio.getSalada(),
            cardapio.getSobremesa(),
            cardapio.getSuco(),
            cardapio.getCriadoEm(),
            cardapio.getAtualizadoEm()
        );
    }
}
