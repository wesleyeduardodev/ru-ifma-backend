package br.edu.ifma.ru.repository;

import br.edu.ifma.ru.entity.Cardapio;
import br.edu.ifma.ru.entity.TipoRefeicao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CardapioRepository extends JpaRepository<Cardapio, Long> {
    List<Cardapio> findByData(LocalDate data);
    Optional<Cardapio> findByDataAndTipoRefeicao(LocalDate data, TipoRefeicao tipoRefeicao);
}
