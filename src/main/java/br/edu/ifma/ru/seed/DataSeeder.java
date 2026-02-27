package br.edu.ifma.ru.seed;

import br.edu.ifma.ru.entity.Administrador;
import br.edu.ifma.ru.repository.AdministradorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdministradorRepository repository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdministradorRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (repository.findByEmail("admin@ifma.edu.br").isEmpty()) {
            Administrador admin = new Administrador(
                "Administrador",
                "admin@ifma.edu.br",
                passwordEncoder.encode("admin123")
            );
            repository.save(admin);
            System.out.println(">>> Admin padrão criado: admin@ifma.edu.br / admin123");
        }
    }
}
