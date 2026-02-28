package br.edu.ifma.ru.seed;

import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.entity.Cardapio;
import br.edu.ifma.ru.entity.TipoRefeicao;
import br.edu.ifma.ru.repository.AdministradorRepository;
import br.edu.ifma.ru.repository.CardapioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
public class DataSeeder implements CommandLineRunner {

    private final AdministradorRepository administradorRepository;
    private final CardapioRepository cardapioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdministradorRepository administradorRepository,
                      CardapioRepository cardapioRepository,
                      PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.cardapioRepository = cardapioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        criarAdminPadrao();
        criarCardapiosExemplo();
    }

    private void criarAdminPadrao() {
        if (administradorRepository.findByEmail("admin@ifma.edu.br").isPresent()) {
            return;
        }

        Administrador admin = new Administrador(
                "Administrador",
                "admin@ifma.edu.br",
                passwordEncoder.encode("admin123")
        );
        administradorRepository.save(admin);
    }

    private void criarCardapiosExemplo() {
        LocalDate hoje = LocalDate.now();

        String[][] almocos = {
                {"Frango grelhado", "Arroz e feijao", "Alface e tomate", "Banana", "Suco de acerola"},
                {"Bife acebolado", "Arroz e feijao preto", "Repolho com cenoura", "Laranja", "Suco de caju"},
                {"Peixe assado", "Arroz e feijao", "Salada de pepino", "Melancia", "Suco de manga"},
                {"Frango ao molho", "Macarrao e feijao", "Alface e beterraba", "Maca", "Suco de goiaba"},
                {"Carne moida", "Arroz e feijao", "Salada de tomate", "Pudim", "Suco de maracuja"},
                {"Filé de frango", "Arroz e feijao tropeiro", "Salada verde", "Gelatina", "Suco de laranja"},
                {"Feijoada", "Arroz branco e couve", "Vinagrete", "Laranja", "Suco de limao"},
        };

        String[][] jantares = {
                {"Sopa de legumes", "Pao frances", "Salada mista", "Gelatina", "Suco de laranja"},
                {"Cachorro-quente", "Batata frita", "Salada de alface", "Fruta da estacao", "Suco de acerola"},
                {"Canja de galinha", "Torrada", "Salada de repolho", "Banana", "Suco de goiaba"},
                {"Sanduiche natural", "Sopa de feijao", "Salada verde", "Maca", "Suco de caju"},
                {"Omelete", "Arroz e feijao", "Salada de tomate", "Melancia", "Suco de manga"},
                {"Panqueca de carne", "Arroz e feijao", "Alface e cenoura", "Pudim", "Suco de maracuja"},
                {"Sopa de frango", "Pao integral", "Salada mista", "Gelatina", "Suco de limao"},
        };

        for (int i = 0; i < 7; i++) {
            LocalDate dia = hoje.plusDays(i);

            if (cardapioRepository.findByDataAndTipoRefeicao(dia, TipoRefeicao.ALMOCO).isEmpty()) {
                Cardapio almoco = new Cardapio();
                almoco.setData(dia);
                almoco.setTipoRefeicao(TipoRefeicao.ALMOCO);
                almoco.setPratoPrincipal(almocos[i][0]);
                almoco.setAcompanhamento(almocos[i][1]);
                almoco.setSalada(almocos[i][2]);
                almoco.setSobremesa(almocos[i][3]);
                almoco.setSuco(almocos[i][4]);
                cardapioRepository.save(almoco);
            }

            if (cardapioRepository.findByDataAndTipoRefeicao(dia, TipoRefeicao.JANTAR).isEmpty()) {
                Cardapio jantar = new Cardapio();
                jantar.setData(dia);
                jantar.setTipoRefeicao(TipoRefeicao.JANTAR);
                jantar.setPratoPrincipal(jantares[i][0]);
                jantar.setAcompanhamento(jantares[i][1]);
                jantar.setSalada(jantares[i][2]);
                jantar.setSobremesa(jantares[i][3]);
                jantar.setSuco(jantares[i][4]);
                cardapioRepository.save(jantar);
            }
        }
    }
}
