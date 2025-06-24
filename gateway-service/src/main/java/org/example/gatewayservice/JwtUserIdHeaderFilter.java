package org.example.gatewayservice;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * A custom Spring Cloud Gateway filter that:
 * - Intercepts incoming HTTP requests
 * - Parses and validates a JWT token from the Authorization header
 * - Extracts the userId from the token
 * - Adds the userId to the request headers as "User-Id"
 * If the token is invalid or missing, the filter stops the request and returns a 401 Unauthorized response.
 */
@Component
public class JwtUserIdHeaderFilter extends AbstractGatewayFilterFactory<JwtUserIdHeaderFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtUserIdHeaderFilter.class);

    /** The secret key used to sign and verify JWT tokens.  */
    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtUserIdHeaderFilter() {
        super(Config.class);
    }

    /**
     * Applies the filter logic to the incoming request. Validates the JWT and injects userId into headers.
     *
     * @param config Configuration object (unused in this filter)
     * @return A GatewayFilter that validates JWT and add the userId to the header
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            logger.info("Incoming request to URI: {}", exchange.getRequest().getURI());
            logger.info("Authorization header: {}", authHeader);

            // Check for presence of token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return handleUnauthorized(exchange);
            }

           // Decrypt and parse the token
            try {
                String token = authHeader.substring(7);
                SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(token)
                        .getBody();

                //Extract userId
                String userId = claims.getSubject();
                if (userId == null || userId.trim().isEmpty()) {
                    logger.warn("JWT valid but missing userId in subject");
                    return handleUnauthorized(exchange);
                }
                logger.info("JWT valid. Extracted userId: {}", userId);
                //Add userId in Header
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("User-Id", userId)
                        .build();

                return chain.filter(exchange.mutate().request(mutatedRequest).build());

            } catch (Exception e) {
                logger.warn("JWT parsing failed: {}", e.getMessage());
                return handleUnauthorized(exchange);
            }
        };
    }

    /**
     * Handles unauthorized requests by setting the HTTP status to 401 and completing the response.
     *
     * @param exchange The current server exchange
     * @return A Mono that completes the response
     */
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {
        // Config class required, even if empty
    }
}
