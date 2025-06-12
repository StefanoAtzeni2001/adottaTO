package com.example.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Configures the Spring application's security, including authentication
 * via form login and OAuth2, logout handling, and CSRF protection.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * - Allows public access to /register and /login
     * - Requires authentication for all other endpoints
     * - Configures form login, OAuth2 login, and logout
     * - Disables CSRF protection (e.g., useful for REST APIs)
     *
     * @param http the HttpSecurity object provided by Spring
     * @return the configured SecurityFilterChain
     * @throws Exception if any configuration error occurs
     */
    @Bean
    public SecurityFilterChain configureSecurity(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(REGISTER_PAGE, LOGIN_PAGE, LOGIN_API, "/api/register").permitAll()
                        .anyRequest().authenticated());

        configureFormLogin(http);
        configureOAuth2Login(http);
        configureLogout(http);

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Configures authentication via form login.
     * <p>
     * - Sets a custom login page
     * - Defines success and failure URLs
     * - Makes the login form publicly accessible
     *
     * @param http the HttpSecurity object
     * @throws Exception if any error occurs during configuration
     */
    private void configureFormLogin(HttpSecurity http) throws Exception {
        http.formLogin(form -> form
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(USER_PAGE, true)
                .failureUrl(LOGIN_PAGE + "?error=true")
                .permitAll());
    }

    /**
     * Configures authentication via OAuth2 (e.g., Google).
     * <p>
     * - Uses the same custom login page
     * - Sets the redirect URL after successful login
     *
     * @param http the HttpSecurity object
     * @throws Exception if any error occurs during configuration
     */
    private void configureOAuth2Login(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(GOOGLE_REGISTRATION, true));
    }

    /**
     * Configures user logout handling.
     * <p>
     * - Sets the logout URL, and the success redirects URL
     * - Makes the logout endpoint publicly accessible
     *
     * @param http the HttpSecurity object
     * @throws Exception if any error occurs during configuration
     */
    private void configureLogout(HttpSecurity http) throws Exception {
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher(LOGOUT_PAGE))
                .logoutSuccessUrl(LOGIN_PAGE + "?logout")
                .permitAll());
    }

    /**
     * Provides a PasswordEncoder bean using the BCrypt algorithm.
     * <p>
     * This encoder is used by Spring Security to hash passwords before storing them.
     *
     * @return a BCrypt-based PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
