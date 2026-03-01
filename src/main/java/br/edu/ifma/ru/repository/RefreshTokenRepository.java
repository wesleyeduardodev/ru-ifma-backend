package br.edu.ifma.ru.repository;

import br.edu.ifma.ru.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByAdministradorId(Long administradorId);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiraEm < :agora")
    void deletarExpirados(Instant agora);
}
