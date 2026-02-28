package br.edu.ifma.ru.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, RequestInfo> loginRequests = new ConcurrentHashMap<>();
    private final Map<String, RequestInfo> generalRequests = new ConcurrentHashMap<>();

    private static final int LOGIN_LIMIT = 5;
    private static final int GENERAL_LIMIT = 60;
    private static final long WINDOW_MS = 60_000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String clientIp = request.getRemoteAddr();
        String path = request.getRequestURI();

        if (path.equals("/api/auth/login") && "POST".equalsIgnoreCase(request.getMethod())) {
            if (excedeuLimite(loginRequests, clientIp, LOGIN_LIMIT)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\":\"Muitas tentativas de login. Tente novamente em 1 minuto.\"}");
                return;
            }
        } else if (path.startsWith("/api/")) {
            if (excedeuLimite(generalRequests, clientIp, GENERAL_LIMIT)) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\":\"Limite de requisicoes excedido. Tente novamente em breve.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean excedeuLimite(Map<String, RequestInfo> mapa, String ip, int limite) {
        long agora = System.currentTimeMillis();
        RequestInfo info = mapa.compute(ip, (key, existing) -> {
            if (existing == null || agora - existing.windowStart > WINDOW_MS) {
                return new RequestInfo(agora, new AtomicInteger(1));
            }
            existing.counter.incrementAndGet();
            return existing;
        });
        return info.counter.get() > limite;
    }

    @Scheduled(fixedRate = 120_000)
    public void limparEntradasExpiradas() {
        long agora = System.currentTimeMillis();
        loginRequests.entrySet().removeIf(entry -> agora - entry.getValue().windowStart > WINDOW_MS);
        generalRequests.entrySet().removeIf(entry -> agora - entry.getValue().windowStart > WINDOW_MS);
    }

    private static class RequestInfo {
        final long windowStart;
        final AtomicInteger counter;

        RequestInfo(long windowStart, AtomicInteger counter) {
            this.windowStart = windowStart;
            this.counter = counter;
        }
    }
}
