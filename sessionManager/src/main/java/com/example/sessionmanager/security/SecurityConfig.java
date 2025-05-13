package com.example.sessionmanager.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("→ SecurityConfig ATTIVA (Spring Security 6)");

        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permetti tutto per ora
                )
                .formLogin(form -> form
                        .loginPage("/login") // la tua pagina HTML personalizzata
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout").permitAll()
                )
                .csrf(csrf -> csrf.disable()); // solo per sviluppo

        return http.build();
    }
}
