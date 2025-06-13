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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

import static com.example.authservice.constants.AuthEndpoints.*;

@Configuration
public class SecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                        HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(REGISTER_PAGE, LOGIN_PAGE, LOGIN_API, "/api/register", "/api/oauth-jwt", "/profile", "/api/profile/update").permitAll()
                        .anyRequest().authenticated()
                );

        configureFormLogin(http);
        configureOAuth2Login(http);
        configureLogout(http);

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(USER_PAGE, true)
                .failureUrl(LOGIN_PAGE + "?error=true")
                .permitAll());
    }

    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage(LOGIN_PAGE)
                .authorizationEndpoint(endpoint -> endpoint
                        .authorizationRequestResolver(customAuthorizationRequestResolver())
                )
                .defaultSuccessUrl(GOOGLE_REGISTRATION, true)
        );
    }

    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PAGE))
                .logoutSuccessUrl(LOGIN_PAGE + "?logout")
                .permitAll());
    }

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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
