package br.edu.ifma.ru.service;

import br.edu.ifma.ru.entity.RefreshToken;
import br.edu.ifma.ru.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenService {

    private final SecretKey chave;
    private final long accessExpiration;
    private final long refreshExpiration;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(@Value("${jwt.secret}") String secret,
                        @Value("${jwt.access-expiration}") long accessExpiration,
                        @Value("${jwt.refresh-expiration}") long refreshExpiration,
                        RefreshTokenRepository refreshTokenRepository) {
        this.chave = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String gerarAccessToken(String email) {
        Instant agora = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plusMillis(accessExpiration)))
                .signWith(chave)
                .compact();
    }

    @Transactional
    public String gerarRefreshToken(Long administradorId) {
        String token = UUID.randomUUID().toString();
        RefreshToken refreshToken = new RefreshToken(
                token,
                administradorId,
                Instant.now().plusMillis(refreshExpiration)
        );
        refreshTokenRepository.save(refreshToken);
        return token;
    }

    public String extrairEmail(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(chave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public Optional<RefreshToken> validarRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> rt.getExpiraEm().isAfter(Instant.now()));
    }

    @Transactional
    public void invalidarRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshTokenRepository::delete);
    }

    @Transactional
    public void invalidarTodosTokens(Long administradorId) {
        refreshTokenRepository.deleteByAdministradorId(administradorId);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void limparTokensExpirados() {
        refreshTokenRepository.deletarExpirados(Instant.now());
    }
}
