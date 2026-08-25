package com.tech.wixblog.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationConverter jwtAuthenticationConverter;

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (
            HttpSecurity http
                                                   ) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                                           session.sessionCreationPolicy(
                                                   SessionCreationPolicy.STATELESS
                                                                        )
                                  )
                .authorizeHttpRequests(auth -> auth
                                               .requestMatchers(
                                                       "/api/v1/auth/register",
                                                       "/api/v1/auth/login",
                                                       "/v3/api-docs/**",
                                                       "/swagger-ui/**",
                                                       "/swagger-ui.html"
                                                               ).permitAll()
                                               .requestMatchers(
                                                       "/api/v1/search/**",
                                                       "/api/v1/tags/**",
                                                       "/api/v1/categories/**"
                                                               )
                                               .permitAll()
                                               .requestMatchers(HttpMethod.GET, "/api/v1/stories/me").authenticated()
                                               .requestMatchers(HttpMethod.GET,
                                                                "/api/v1/users/**",
                                                                "/api/v1/stories/*",
                                                                "/api/v1/feed/following").permitAll()
                                               .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                                               .anyRequest().authenticated()
                                      )
                .oauth2ResourceServer(oauth2 ->
                                              oauth2.jwt(jwt ->
                                                                 jwt.jwtAuthenticationConverter(
                                                                         jwtAuthenticationConverter
                                                                                               )
                                                        )
                                     );
        return http.build();
    }
}