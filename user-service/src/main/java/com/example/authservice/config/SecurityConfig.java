package com.example.authservice.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Configuration class for Spring Security.
 * Sets up endpoint permissions, OAuth2 login handling, CSRF disabling,
 * password encoding, and custom authorization request resolution.
 */
@Configuration
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    /**
     * Constructor-based injection of the client registration repository.
     *
     * @param clientRegistrationRepository repository for OAuth2 client registrations
     */
    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Configures the Spring Security filter chain.
     * - Allows unauthenticated access to specific endpoints
     * - All other requests require authentication
     * - Configures OAuth2 login
     * - Disables CSRF protection
     *
     * @param http HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception in case of misconfiguration
     */
    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(API_LOGIN, API_REGISTER, API_OAUTH_JWT, PROFILE, API_PROFILE_UPDATE, PROFILE_EMAIL, "api/profile/**").permitAll()
                        .anyRequest().authenticated()
                );

        configureOAuth2Login(http);

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configures OAuth2 login with a custom authorization request resolver
     *
     * @param http HttpSecurity object to extend
     * @throws Exception in case of error
     */
    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage(API_LOGIN)
                .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestResolver(customAuthorizationRequestResolver())
                )
                .defaultSuccessUrl(GOOGLE_REGISTRATION, true)
        );
    }

    /**
     * Custom resolver for OAuth2 authorization requests
     *
     * @return a custom OAuth2AuthorizationRequestResolver
     */
    private OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver() {
        DefaultOAuth2AuthorizationRequestResolver defaultResolver =
                new DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        return new OAuth2AuthorizationRequestResolver() {
            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
                return customizeRequest(req);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
                return customizeRequest(req);
            }

            private OAuth2AuthorizationRequest customizeRequest(OAuth2AuthorizationRequest req) {
                if (req == null) return null;

                Map<String, Object> additionalParameters = new HashMap<>(req.getAdditionalParameters());

                // Imposta il comportamento desiderato dopo logout:
                // "consent" = forza selezione account / autorizzazione
                // "login" = forza reinserimento credenziali
                additionalParameters.put("prompt", "consent");

                return OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(additionalParameters)
                        .build();
            }
        };
    }

    /**
     * Defines the password encoder bean using BCrypt.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the authentication manager bean required by Spring Security.
     *
     * @param config the authentication configuration provided by Spring
     * @return the AuthenticationManager instance
     * @throws Exception if manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
