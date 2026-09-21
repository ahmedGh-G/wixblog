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
                                                       "/auth/register",
                                                       "/auth/login",
                                                       "/v3/api-docs/**",
                                                       "/swagger-ui/**",
                                                       "/swagger-ui.html"
                                                               ).permitAll()
                                               .requestMatchers(
                                                       "/search/**",
                                                       "/tags/**",
                                                       "/categories/**"
                                                               )
                                               .permitAll()
                                               .requestMatchers(HttpMethod.GET,
                                                                "/users/**",
                                                                "/stories/*").permitAll()
                                               .requestMatchers(HttpMethod.GET,
                                                                "/stories/me",
                                                                "/feed/**").authenticated()
                                               .requestMatchers("/recommendations/**").authenticated()
                                               .requestMatchers("/admin/**").hasRole("ADMIN")
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