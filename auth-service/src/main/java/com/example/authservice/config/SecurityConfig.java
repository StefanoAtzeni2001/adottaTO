package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Configura la sicurezza dell'applicazione Spring, inclusa l'autenticazione
 * tramite form e OAuth2, la gestione del logout e la protezione CSRF.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configura la catena di filtri di sicurezza per l'applicazione.
     * <p>
     * - Permette l'accesso pubblico a /register e /login
     * - Richiede autenticazione per tutte le altre richieste
     * - Configura login form, login OAuth2 e logout
     * - Disabilita CSRF (per esempio per API REST)
     *
     * @param http oggetto HttpSecurity fornito da Spring
     * @return SecurityFilterChain configurata
     * @throws Exception se si verifica un errore nella configurazione
     */
    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(REGISTER_PAGE, LOGIN_PAGE).permitAll()
                        .anyRequest().authenticated());

        configureFormLogin(http);
        configureOAuth2Login(http);
        configureLogout(http);

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configura l'autenticazione tramite form login.
     * <p>
     * - Imposta la pagina di login personalizzata
     * - Definisce la URL di successo e di errore
     * - Rende il form di login accessibile a tutti
     *
     * @param http oggetto HttpSecurity
     * @throws Exception se si verifica un errore nella configurazione
     */
    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(USER_PAGE, true)
                .failureUrl(LOGIN_PAGE + "?error=true")
                .permitAll());
    }

    /**
     * Configura l'autenticazione tramite OAuth2 (es. Google).
     * <p>
     * - Usa la stessa pagina di login personalizzata
     * - Imposta la URL di redirect dopo successo
     *
     * @param http oggetto HttpSecurity
     * @throws Exception se si verifica un errore nella configurazione
     */
    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(GOOGLE_REGISTRATION, true));
    }

    /**
     * Configura il logout dell'utente.
     * <p>
     * - Definisce la URL di logout e la URL di successo
     * - Rende il logout accessibile a tutti
     *
     * @param http oggetto HttpSecurity
     * @throws Exception se si verifica un errore nella configurazione
     */
    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PAGE))
                .logoutSuccessUrl(LOGIN_PAGE + "?logout")
                .permitAll());
    }

    /**
     * Fornisce un bean PasswordEncoder usando l'algoritmo BCrypt.
     * <p>
     * È utilizzato da Spring Security per codificare le password prima di salvarle.
     *
     * @return PasswordEncoder basato su BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
