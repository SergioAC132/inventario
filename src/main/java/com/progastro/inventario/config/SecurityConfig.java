package com.progastro.inventario.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Solo ADMIN
                .requestMatchers(HttpMethod.GET, "/api/usuarios/me").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/compras/*/cancelar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/roles/**").hasRole("ADMIN")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // ADMIN y EDITOR pueden editar
                .requestMatchers(HttpMethod.POST, "/api/compras/editar-compra").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.POST, "/api/salidas/editar-salida").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.POST, "/api/productos/editar-producto").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.POST, "/api/marcas/editar-marca").hasAnyRole("ADMIN", "EDITOR")
                .requestMatchers(HttpMethod.PATCH, "/api/inventarios/*/*").hasAnyRole("ADMIN", "EDITOR")

                // ADMIN, EDITOR y CAPTURISTA pueden registrar
                .requestMatchers(HttpMethod.POST, "/api/compras/registrar-compra").hasAnyRole("ADMIN", "EDITOR", "CAPTURISTA")
                .requestMatchers(HttpMethod.POST, "/api/salidas/registrar-salida").hasAnyRole("ADMIN", "EDITOR", "CAPTURISTA")
                .requestMatchers(HttpMethod.POST, "/api/productos/registrar-producto").hasAnyRole("ADMIN", "EDITOR", "CAPTURISTA")
                .requestMatchers(HttpMethod.POST, "/api/marcas/registrar-marca").hasAnyRole("ADMIN", "EDITOR", "CAPTURISTA")

                // Todos los roles pueden consultar
                .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "EDITOR", "CAPTURISTA", "VISOR")

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        int strength = 12;
        return new BCryptPasswordEncoder(strength);
    }
}
