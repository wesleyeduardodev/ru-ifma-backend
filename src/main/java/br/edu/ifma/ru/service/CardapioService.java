package br.edu.ifma.ru.service;

import br.edu.ifma.ru.dto.request.CardapioRequest;
import br.edu.ifma.ru.dto.response.CardapioResponse;
import br.edu.ifma.ru.entity.Cardapio;
import br.edu.ifma.ru.exception.ResourceNotFoundException;
import br.edu.ifma.ru.repository.CardapioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardapioService {

    private final CardapioRepository repository;

    public CardapioService(CardapioRepository repository) {
        this.repository = repository;
    }

    public List<CardapioResponse> listarPorData(LocalDate data) {
        return repository.findByData(data).stream()
                .map(CardapioResponse::fromEntity)
                .toList();
    }

    public CardapioResponse buscarPorId(Long id) {
        Cardapio cardapio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado com id: " + id));
        return CardapioResponse.fromEntity(cardapio);
    }

    public CardapioResponse criar(CardapioRequest request) {
        Cardapio cardapio = new Cardapio();
        cardapio.setData(request.data());
        cardapio.setTipoRefeicao(request.tipoRefeicao());
        cardapio.setPratoPrincipal(request.pratoPrincipal());
        cardapio.setAcompanhamento(request.acompanhamento());
        cardapio.setSalada(request.salada());
        cardapio.setSobremesa(request.sobremesa());
        cardapio.setSuco(request.suco());
        Cardapio salvo = repository.save(cardapio);
        return CardapioResponse.fromEntity(salvo);
    }

    public CardapioResponse atualizar(Long id, CardapioRequest request) {
        Cardapio cardapio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cardápio não encontrado com id: " + id));
        cardapio.setData(request.data());
        cardapio.setTipoRefeicao(request.tipoRefeicao());
        cardapio.setPratoPrincipal(request.pratoPrincipal());
        cardapio.setAcompanhamento(request.acompanhamento());
        cardapio.setSalada(request.salada());
        cardapio.setSobremesa(request.sobremesa());
        cardapio.setSuco(request.suco());
        Cardapio salvo = repository.save(cardapio);
        return CardapioResponse.fromEntity(salvo);
    }

    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cardápio não encontrado com id: " + id);
        }
        repository.deleteById(id);
    }
}
