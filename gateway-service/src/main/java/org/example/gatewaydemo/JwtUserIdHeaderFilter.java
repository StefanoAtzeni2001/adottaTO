package org.example.gatewaydemo;



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

//filtro che controlla il token jwt, estrae il nome utente e lo inserisce nell'header
@Component
public class JwtUserIdHeaderFilter extends AbstractGatewayFilterFactory<JwtUserIdHeaderFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(JwtUserIdHeaderFilter.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    public JwtUserIdHeaderFilter() {
        super(Config.class);
    }

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
    //handle errors with token, block routing and return 401
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    public static class Config {
        // Config class required, even if empty
    }
}
