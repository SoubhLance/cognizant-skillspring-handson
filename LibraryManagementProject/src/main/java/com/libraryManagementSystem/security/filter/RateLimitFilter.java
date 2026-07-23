package com.libraryManagementSystem.security.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate-limiting filter for sensitive auth endpoints.
 *
 * Strategy: Token Bucket — 10 tokens per minute per IP.
 * Applies only to: /api/auth/login and /api/auth/register
 * Returns HTTP 429 Too Many Requests with a Retry-After header when throttled.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    /** One bucket per client IP, lazily initialised. */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /** 10 requests per 60 seconds per IP. */
    private Bucket newBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Only rate-limit auth endpoints
        if (!uri.startsWith("/api/auth/login") && !uri.startsWith("/api/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = resolveClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> newBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Too many requests. Please try again in 60 seconds.\",\"errorCode\":\"RATE_LIMIT_EXCEEDED\"}"
            );
        }
    }

    /**
     * Resolves the real client IP, honouring X-Forwarded-For for reverse proxies.
     */
    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            // Take only the first IP in the chain (client IP)
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
